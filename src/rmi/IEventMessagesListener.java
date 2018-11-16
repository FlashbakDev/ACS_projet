package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Benjamin
 */
public interface IEventMessagesListener extends Remote {

    public void EventMessageReceived(String message) throws RemoteException;
}
