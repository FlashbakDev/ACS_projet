package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
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

        client.onEventMessageReceived(message);
    }
    
    @Override
    public void EventEnd(Bet clientBet, Bet result, Map<Player, Integer> votes) throws RemoteException {

        client.onEventEnd(clientBet, result, votes);
    }
    
    @Override
    public void Kick(String message) throws RemoteException{
        
        client.onKickedByServer(message);
    }
}