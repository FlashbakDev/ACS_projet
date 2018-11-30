package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Benjamin
 * Classe qui Gere les messages du serveur vers les clients.
 */
public class ClientListener extends UnicastRemoteObject implements IClientListener {

    private final Client client;

    public ClientListener(Client client){
        
        this.client = client;
    }

    @Override
    public void EventMessage(String message) throws RemoteException {

        client.onEventMessageReceived(message);
    }
    
    @Override
    public void EventEnd() throws RemoteException {

        client.onEventEnd();
    }
}