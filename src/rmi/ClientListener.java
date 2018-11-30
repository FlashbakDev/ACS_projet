package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Benjamin
 * Classe qui Gere les messages du serveur vers les clients.
 */
public class ClientListener extends UnicastRemoteObject implements IClientListener {

    private final Client client;

    public ClientListener(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void EventMessage(String message) throws RemoteException {

        client.onMessageReceived(message);
    }
     @Override
    public void EventEnd() throws RemoteException {

        client.onFinDuMatch();
    }

    @Override
    public void EventGoodVote() throws RemoteException {
        client.onMessageReceived("Felicitation, tu as voté pour le meilleur joueur ! ");
    }

    @Override
    public void EventGoodBet() throws RemoteException {
       client.onMessageReceived("Felicitation, tu as predit le resultat ! ");
    }
}
