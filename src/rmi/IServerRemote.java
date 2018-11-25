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

    public String test() throws RemoteException;

    public long connect(IEventMessagesListener listener) throws RemoteException;

    public boolean disconnect(long id) throws RemoteException;

    public Map<Player, Integer> getPlayersList() throws RemoteException;
    public Set<String> getPariList() throws RemoteException;
    
    public boolean vote(long id, Player j) throws RemoteException;
    
    public boolean pari(long id, String j) throws RemoteException;
    
    public List<String> getPassedLines()throws RemoteException;
}
