/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import client.Client;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author Benjamin
 */
public class ClientRemote extends UnicastRemoteObject implements IMessageListener{

    private Client client;
    
    public ClientRemote(Client client) throws RemoteException
    {
        this.client = client;
    } 
    
    @Override
    public void messageReceived(String message) throws RemoteException {
        
        System.out.println("rmi.ClientRemote.messageReceived() : "+ message);
        
        client.onMessageReceived(message);
    }
}
