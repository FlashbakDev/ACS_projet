package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Benjamin
 */
public class EventMessagesListener extends UnicastRemoteObject implements IEventMessagesListener {

    private final Client client;

    public EventMessagesListener(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void EventMessageReceived(String message) throws RemoteException {

        client.onMessageReceived(message);
    }
     @Override
    public void EventFinDuMatch() throws RemoteException {

        client.onFinDuMatch();
    }

    @Override
    public void EventVoteGagnant() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void EventPariGagnant() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
