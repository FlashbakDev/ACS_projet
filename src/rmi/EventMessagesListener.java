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
}
