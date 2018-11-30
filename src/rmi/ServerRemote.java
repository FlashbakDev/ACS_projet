package rmi;

import java.io.IOException;
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
 * Classe communiquante rmi, le client utilise ses methodes pour les actions sur 
 * le serveur. 
 * @author Benjamin
 * @version 1.1
 */
public class ServerRemote extends UnicastRemoteObject implements IServerRemote {

    private final EventsManager eventsManager;
    private final Map<Long, ClientInst> clients;
    private long ids;

    /**
     * Initialise le manager d'evenement.
     * @since 1.0
     * @throws RemoteException
     */
    public ServerRemote() throws RemoteException, IOException{
        super();
       
            eventsManager = new EventsManager(this, "Events/test.txt");
            clients = new HashMap<>();
            ids = 0;

            eventsManager.start();
        
    }


    /** 
     * Fonction qui s'execute à chaque connexion de client
     * Verifie dans la table des clients si le client qui se connecte est
     * nouveau ou ancien.
     *
     * @param listener : la classe permettant la comunications du serveur au client
     * @return L'id du client, si négative il y a eu une erreur
     * @throws java.rmi.RemoteException si le rmiregistry 'est inactif
     * @since 1.0
     */
    @Override
    public long connect(IEventMessagesListener listener) throws RemoteException {

        try {

            String ip = getClientHost();

            System.out.println("[" + ip + "]ServerRemote.connect()");

            // already exists
            for (Map.Entry<Long, ClientInst> entry : clients.entrySet()) {

                if (entry.getValue().getIp().equals(ip)) {

                    if (!entry.getValue().connected) {

                        System.out.println("[" + ip + "] reconnected");

                        entry.getValue().connected = true;
                        entry.getValue().listener = listener;

                        return entry.getValue().getId();
                    }else{
                        System.out.println("[" + ip + "] already connected");
                        return -2;
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
     * Ne le supprime pas des listes de clients
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
     * Incremente les id des clients
     * @since 1.0
     */
    private long getNextId() {

        ids++;
        return ids;
    }

    /**
     * Envoie un message à tous les clients connecté
     * @param message : Le message à envoyer aux clients
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
     * S'active à la fin d'un match. Avertie les clients de l'arret du match
     * Envoie aux clients concerné les infos sur les reussites aux pari ou vote
     * 
     * @param joueurs : La map des joueurs ex aequo ayant eu le plus de vote
     * @param resulat : Le resultat du match
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
                    if(joueurs.containsKey(entry.getValue().vote)){
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
