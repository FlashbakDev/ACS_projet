package rmi;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * @since 1.0
     * @throws RemoteException
     */
    public ServerRemote() throws RemoteException {
        super();

        eventsManager = new EventsManager(this, "Events/test.txt");
        clients = new HashMap<>();
        ids = 0;

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
     *
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
    
    /**
     * @since 1.0
     */
    public void finDuMatch(String resulat, Map<Player, Integer> joueurs){
         clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {
                    
                    entry.getValue().listener.EventFinDuMatch();
                    if(entry.getValue().pari.equals(resulat)){
                        entry.getValue().listener.EventPariGagnant();
                    }
                    if(joueurs.containsValue(entry.getValue().vote)){
                        entry.getValue().listener.EventVoteGagnant();
                    }
                } catch (RemoteException|NullPointerException  ex) {

                    Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);

                    // client is disconnected
                    entry.getValue().connected = false;
                    entry.getValue().listener = null;
                }
            }
        });
    }

    /**
     * @return la List de joueurs unique sur le serveur
     * @throws java.rmi.RemoteException
     * @since 1.1
     */
    @Override
    public Map<Player, Integer> getPlayersList() throws RemoteException{
        return eventsManager.getPlayersVotes();
    }
    
      /**
     * @return la List de joueurs unique sur le serveur
     * @throws java.rmi.RemoteException
     * @since 1.1
     */
    @Override
   public Set<String> getPariList() throws RemoteException{
        return eventsManager.getPari();
    }
    /**
     * Un vote est pris en compte uniquement si le match n'est pas terminé
     * @param id : L'identifiant du client.
     * @param j : L'identifiant du joueur
     * @return : indicateur de validité du vote.
     * @throws RemoteException
     */
    @Override
    public boolean vote(long id, Player j) throws RemoteException{
        
        if(j==null)return false;
        if(this.eventsManager.getMatch_en_cour() && this.eventsManager.vote(j)){
            
            this.eventsManager.unvote(this.clients.get(id).vote);
            this.clients.get(id).vote = j;
            
            System.out.println( "["+id+"] voted for "+ j.toString());
            return true;
        }
        
        System.out.println( "["+id+"] invalid vote : "+ j.toString());
        return false;
    }
    
     /**
     * Un pari est pris en compte uniquement si le match n'est pas terminé
     * @param id : L'identifiant du client.
     * @param j : nom du pari
     * @return : indicateur de validité du pari.
     * @throws RemoteException
     */
    @Override
    public boolean pari(long id, String j) throws RemoteException{
        
        if(j == null)return false;
        
        if(this.eventsManager.getMatch_en_cour()){
            this.clients.get(id).pari=(j);
            
            System.out.println( "["+id+"] paried for "+ j.toString());
            return true;
        }
        
        System.out.println( "["+id+"] invalid pari : "+ j.toString());
        return false;
    }
    
    
    @Override
    public List<String> getPassedLines()throws RemoteException{
        return this.eventsManager.getPassedLines();
    }
}
