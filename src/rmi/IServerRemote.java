package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Benjamin
 */
public interface IServerRemote extends Remote {

    String test() throws RemoteException;

    long connect(IEventMessagesListener listener) throws RemoteException;

    boolean disconnect(long id) throws RemoteException;
}
