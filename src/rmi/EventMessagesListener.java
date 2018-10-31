/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Benjamin
 */
public class EventMessagesListener extends UnicastRemoteObject implements IEventMessagesListener{

    private final Client client;
    
    public EventMessagesListener(Client client) throws RemoteException
    {
        this.client = client;
    } 
    
    @Override
    public void EventMessageReceived(String message) throws RemoteException {
        
        client.onMessageReceived(message);
    }
}
