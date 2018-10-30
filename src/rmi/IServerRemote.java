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
public interface IServerRemote extends Remote {

    String test() throws RemoteException;
    
    long connect(IMessageListener listener) throws RemoteException;
    
    boolean disconnect(long id) throws RemoteException;
}
