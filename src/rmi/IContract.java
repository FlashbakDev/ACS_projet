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
public interface IContract extends Remote {

    String test() throws RemoteException;
    
    boolean connect() throws RemoteException;
    
    boolean disconnect() throws RemoteException;
}
