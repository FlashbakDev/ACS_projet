package rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
    public long connect(IClientListener listener) throws RemoteException {

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
     * @param joueurs : La map des joueurs ex aequo ayant eu le plus de vote
     * @param resulat : Le resultat du match
     * @since 1.0
     */
    public void finDuMatch(String resulat, Map<Player, Integer> joueurs){
         clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {
                    
                    entry.getValue().listener.EventEnd();
                    if(entry.getValue().pari.equals(resulat)){
                        entry.getValue().listener.EventGoodBet();
                    }
                    if(joueurs.containsKey(entry.getValue().vote)){
                        entry.getValue().listener.EventGoodVote();
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

    @Override
    public Set<Player> getPlayersList(long id) throws RemoteException{
        
        log(this.clients.get(id),"Retrieve players list.");
        
        return eventsManager.getPlayersVotes().keySet();
    }
    
    @Override
    public Set<String> getAvailableBets(long id) throws RemoteException{
       
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
    public boolean bet(long id, String b) throws RemoteException{
        
        if(b == null)
            return false;
        
        ClientInst client = this.clients.get(id);
        
        if(this.eventsManager.getIsEventRunning()){
            
            client.pari = b;
            
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
    
    private void log(ClientInst c, String s){
        
        System.out.println("[" + c.toString() + "] "+ s);
    }
}
