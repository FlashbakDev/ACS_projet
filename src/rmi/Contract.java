/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Benjamin
 */
public class Contract extends UnicastRemoteObject implements IContract {
    
    public Contract() throws RemoteException {
        
        super();
    }

    @Override
    public String test() throws RemoteException {
        
        System.out.println("Contract.test()");
        
        return "Hello world !";
    }
    
    @Override
    public boolean connect() throws RemoteException{
        
        System.out.println("Contract.connect()");
        
        return true;
    }
    
    @Override
    public boolean disconnect() throws RemoteException{
        
        System.out.println("Contract.disconnect()");
        
        return true;
    }
}
