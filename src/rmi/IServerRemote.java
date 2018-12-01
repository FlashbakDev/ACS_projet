package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Benjamin
 */
public interface IServerRemote extends Remote {

    public long connect(IClientListener listener) throws RemoteException;
    public boolean disconnect(long id) throws RemoteException;

    /**
     * @return la List de joueurs unique sur le serveur
     * @throws java.rmi.RemoteException
     * @since 1.1
     */
    public List<Player> getPlayersList(long id) throws RemoteException;

    /**
     * @return la List de joueurs unique sur le serveur
     * @throws java.rmi.RemoteException
     * @since 1.1
     */
    public List<Bet> getAvailableBets(long id) throws RemoteException;
    
    /**
     * Un vote est pris en compte uniquement si le match n'est pas terminé
     * @param id : L'identifiant du client.
     * @param j : L'identifiant du joueur
     * @return : indicateur de validité du vote.
     * @throws RemoteException
     */
    public boolean vote(long id, Player j) throws RemoteException;
    
    /**
     * Un pari est pris en compte uniquement si le match n'est pas terminé
     * @param id : L'identifiant du client.
     * @param b : pari
     * @return : indicateur de validité du pari.
     * @throws RemoteException
     */
    public boolean bet(long id, Bet b) throws RemoteException;
    
    public Bet getBet(long id) throws RemoteException;
    
    public Player getVote(long id) throws RemoteException;
    
    public List<String> getEventHistory(long id)throws RemoteException;
}
