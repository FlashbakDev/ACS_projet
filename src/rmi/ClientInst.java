package rmi;

/**
 *  Classe qui gere les clients (chaque clients se connectant est associer à 
 * une instance "ClientInst" Permet de gere les messages serveur-> clients et 
 * de gerer les votes/pari d'un client
 * @author Benjamin
 */
public class ClientInst {

    private final long id;
    private final String ip;
    
    /**Indique si le client est connecter (le serveur n'envoie pas de messages 
     * aux clients deconnecter)*/
    public boolean connected;
    
    public IClientListener listener;
    
    /**Le joueur pour qui le client à voter.*/
    public Player vote = null; 
    
    /**Le pari sur le resultat du client*/
    public Bet bet = null;

    /**
     * Constructeur de ClientInst
     * @param id : l'identifiant du client
     * @param ip : L'ip du client (sert à l'identifier à la reconnexion )
     * @param listener : L'objet permettant au serveur de communiquer avec les 
     * clients
     */
    public ClientInst(long id, String ip, IClientListener listener) {

        this.id = id;
        this.ip = ip;
        this.connected = true;
        this.listener = listener;
        this.vote = null;
        this.bet = null;
    }

    public String getIp() {
        return ip;
    }

    public long getId() {
        return id;
    }
}
