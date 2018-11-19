package rmi;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benjamin
 * @version 1.1
 */
public class ServerRemote extends UnicastRemoteObject implements IServerRemote {

    private final EventsManager eventsManager;
    private final Map<Long, ClientInst> clients;
    private long ids;
    /**La liste des joueurs, 
     * @since 1.1 */
    private List<Joueur> liste_joueurs;

    /**
     * @since 1.0
     * @throws RemoteException
     */
    public ServerRemote() throws RemoteException {
        super();

        eventsManager = new EventsManager(this, "Events/test.txt");
        clients = new HashMap<>();
        ids = 0;
        
        //Solution temporaire de test.
        try{
            this.liste_joueurs = eventsManager.generateListJoueur();
        }catch(Exception e){
            this.liste_joueurs = new ArrayList<Joueur>();
            liste_joueurs.add(new Joueur("machin"));
            liste_joueurs.add(new Joueur("zidane"));
        }
        

        eventsManager.start();
    }

    
    /**
     * @since 0.1
     * @throws RemoteException
     */
    @Override
    public String test() throws RemoteException {

        try {

            String ip = getClientHost();
            System.out.println("[" + ip + "] ServerRemote.test()");

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Hello world !";
    }

    /** Fonction qui s'execute à chaque connexion de client
     * Verifie dans la table des clients si le client qui se connecte est
     * nouveau ou ancien.
     *
     * @param listener
     * @return
     * @throws java.rmi.RemoteException
     * @since 1.0
     */
    @Override
    public long connect(IEventMessagesListener listener) throws RemoteException {

        try {

            String ip = getClientHost();

            System.out.println("[" + ip + "] ServerRemote.connect()");

            // already exists
            for (Map.Entry<Long, ClientInst> entry : clients.entrySet()) {

                if (entry.getValue().getIp().equals(ip)) {

                    if (!entry.getValue().connected) {

                        System.out.println("[" + ip + "] reconnected");

                        entry.getValue().connected = true;
                        entry.getValue().listener = listener;

                        List<String> lines = eventsManager.getPassedLines();
                        for (String line : lines) {
                            entry.getValue().listener.EventMessageReceived(line);
                        }

                        return entry.getValue().getId();
                    }
                }
            }

            // add new
            ClientInst client = new ClientInst(getNextId(), ip, listener);
            clients.put(client.getId(), client);

            System.out.println("[" + ip + "] connected");

            return client.getId();

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    /**
     * Fonction qui deconnecte un client
     * @param id : L'id du client à deconnecté
     * @return indication de deconnexion du client
     * @throws RemoteException
     * @since 1.0
     * 
     */
    @Override
    public boolean disconnect(long id) throws RemoteException {

        try {

            String ip = getClientHost();

            System.out.println("[" + ip + "] ServerRemote.disconnect()");

            if (clients.containsKey(id)) {

                clients.get(id).connected = false;
                clients.get(id).listener = null;

                System.out.println("[" + ip + "] disconnected");
                return true;
            }

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    //peut etre synchronized ?
    
    /**
     * @since 1.0
     */
    private long getNextId() {

        ids++;
        return ids;
    }

    
    /**
     * @since 1.0
     */
    public void notifyListeners(String message) {

        //System.out.println("ServerRemote.notifyListeners() : " + message);
        clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {

                    entry.getValue().listener.EventMessageReceived(message);

                } catch (RemoteException ex) {

                    Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);

                    // client is disconnected
                    entry.getValue().connected = false;
                    entry.getValue().listener = null;
                }
            }
        });
    }
    
    
    /**@return la List de joueurs unique sur le serveur
     * @since 1.1
     */
    @Override
    public List<Joueur> getListJoueurs() {
       return this.liste_joueurs;
    }
}
