package rmi;

import java.io.IOException;
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
 * Classe communiquante rmi, le client utilise ses methodes pour les actions sur 
 * le serveur. 
 * @author Benjamin
 * @version 1.2
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
    public ServerRemote() throws RemoteException{
        super();
       
        eventsManager = new EventsManager(this);
        clients = new HashMap<>();
        ids = 0;
    }

    /**
     * Incremente les id des clients
     * @since 1.0
     */
    synchronized private long getNextId() {

        ids++;
        return ids;
    }

    /**
     * Envoie un message à tous les clients connecté
     * @param message : Le message à envoyer aux clients
     * @since 1.0
     */
    public void sendEventMessage(String message) {
        
        clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {

                    entry.getValue().listener.EventMessage(message);

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
     * @param votes : La map des joueurs / nombre de votes
     * @param result : Le resultat du match
     * @since 1.0
     */
    public void sendEventEnded(Bet result, Map<Player, Integer> votes){
        
         clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {
                    
                    entry.getValue().listener.EventEnd(entry.getValue().bet, result, votes);
                    
                } catch (RemoteException|NullPointerException  ex) {

                    Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);

                    // client is disconnected
                    entry.getValue().connected = false;
                    entry.getValue().listener = null;
                }
            }
        });
    }

    private void log(ClientInst c, String s){
        
        if(c != null)
            System.out.println("[" + c.toString() + "] "+ s);
        else
            System.out.println(s);
    }
    
    public void startEvent(String eventFileName){
        
        try {
            
            eventsManager.startEvent(eventFileName);
            
        } catch (IOException ex) {
            
            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopEvent(){
        
        eventsManager.stopEvent();
        
        clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {
                    
                    entry.getValue().listener.Kick("Le serveur à été fermé.");
                    
                } catch (RemoteException ex) {
                    
                    Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                entry.getValue().connected = false;
                entry.getValue().listener = null;
            }
        });
    }
    
    public void quit(){
        
        stopEvent();
    }
    
    //==========================================================================
    // IServerRemote
    //==========================================================================
    
        /** 
     * Fonction qui s'execute à chaque connexion de client
     * Verifie dans la table des clients si le client qui se connecte est
     * nouveau ou ancien.
     *
     * @param listener : la classe permettant la comunications du serveur au client
     * @return L'id du client, -1 si erreur
     * @throws java.rmi.RemoteException si le rmiregistry est inactif
     * @since 1.0
     */
    @Override
    public long connect(IClientListener listener) throws RemoteException {

        if(!eventsManager.getIsEventRunning())
            return -2;
        
        try {

            String ip = getClientHost();
            log( null, "Connection from "+ ip+"...");

            // already exists
            for (Map.Entry<Long, ClientInst> entry : clients.entrySet()) {

                // check ip
                if (entry.getValue().getIp().equals(ip)) {

                    if (!entry.getValue().connected) {

                        entry.getValue().connected = true;
                        entry.getValue().listener = listener;

                        log(entry.getValue(), "Reconnected.");
                        return entry.getValue().getId();
                    }
                }
            }

            // add new
            ClientInst client = new ClientInst(getNextId(), ip, listener);
            clients.put(client.getId(), client);

            log(client, "Connected.");

            return client.getId();

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Unknown error
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

        ClientInst client = clients.get(id);

        log( client, "Disconnect...");

        if (clients.containsKey(id)) {

            clients.get(id).connected = false;
            clients.get(id).listener = null;

            log( client, "Disconnected.");
            return true;
        }

        return false;
    }
    
    @Override
    public List<Player> getPlayersList(long id) throws RemoteException{
        
        log(this.clients.get(id),"Retrieve players list.");
        
        return new ArrayList<>(eventsManager.getPlayersVotes().keySet());
    }
    
    @Override
    public List<Bet> getAvailableBets(long id) throws RemoteException{
       
        log(this.clients.get(id),"Retrieve available bet.");
        
        return eventsManager.getAvailableBets();
    }
    
    @Override
    public boolean vote(long id, Player j) throws RemoteException{
        
        if(j == null)
            return false;
        
        ClientInst client = this.clients.get(id);
        
        if(this.eventsManager.vote(j)){
            
            // Annulation du vote précédent et enregistrement du nouveau
            this.eventsManager.unvote(client.vote);
            client.vote = j;
            
            log(client, "voted for "+ j );
            return true;
        }
        
        log(client, "Invalid vote ("+ j +")");
        return false;
    }
    
    @Override
    public boolean bet(long id, Bet b) throws RemoteException{
        
        if(b == null)
            return false;
        
        ClientInst client = this.clients.get(id);
        
        if(this.eventsManager.getIsEventRunning()){
            
            client.bet = b;
            
            log( client, "bet for "+ b );
            return true;
        }
        
        log( client, "bet error ("+ b +")" );
        return false;
    }
    
    @Override
    public List<String> getEventHistory(long id)throws RemoteException{
        
        log(this.clients.get(id),"Retrieve event history.");
        
        return this.eventsManager.getHistory();
    }
    
    @Override
    public Bet getBet(long id) throws RemoteException{
        
        return clients.get(id).bet;
    }
    
    @Override
    public Player getVote(long id) throws RemoteException{
     
        return clients.get(id).vote;
    }
}
