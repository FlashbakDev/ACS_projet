/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import rmi.EventsManager;
import rmi.ServerRemote;

/**
 *
 * @author Benjamin
 */
public class Server {

    private ServerRemote serverRemote;
    
    public Server() {
        
        serverRemote = null;
    }
    
    public void Start(){
        
        System.out.println("Server start");
        Init();
    }
    
    public void Init(){
        
        System.out.println("Server init...");
        
        try {

            serverRemote = new ServerRemote();
            System.out.println("Object created.");
            
            Naming.rebind("serverRemote", serverRemote);
            System.out.println("Object saves.");
            
            System.out.println("Server ready.");

        } catch (MalformedURLException | RemoteException e) {

            System.out.println(e);
        }
    }
 
    public static void main(String[] args) {

        Server server = new Server();
        server.Start();
    }
}
