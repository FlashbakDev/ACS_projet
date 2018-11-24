package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Benjamin
 */
public interface IServerRemote extends Remote {

    public String test() throws RemoteException;

    public long connect(IEventMessagesListener listener) throws RemoteException;

    public boolean disconnect(long id) throws RemoteException;

    public Map<Player, Integer> getPlayersList() throws RemoteException;
    
    public boolean vote(long id, Player j) throws RemoteException;
}
