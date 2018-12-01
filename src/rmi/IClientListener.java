package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Benjamin
 */
public interface IClientListener extends Remote {

    public void EventMessage(String message) throws RemoteException;
    public void EventEnd() throws RemoteException;
    public void Kick(String message) throws RemoteException;
}
