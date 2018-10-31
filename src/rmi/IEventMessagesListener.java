/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Benjamin
 */
public interface IEventMessagesListener extends Remote{
    
    public void EventMessageReceived(String message) throws RemoteException;
}
