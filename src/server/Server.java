/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rmi.ServerRemote;

/**
 * @author Benjamin
 */
public class Server {

    //==========================================================================
    // Main
    //==========================================================================
    
    public static void main(String[] args) {

        Server server = new Server();
        server.start();
    }
    
    //==========================================================================
    // Server
    //==========================================================================
    
    private ServerRemote serverRemote;

    public Server() {

        serverRemote = null;
    }

    /**
     * Initialise le serveur 
     * Peut provoquer des erreurs si le rmiregistry n'est pas actif
     * ou si le fichier d'evenement n'est pas valide
     */
    public void start() {

        System.out.println("Server started, initialization...");

        try {

            serverRemote = new ServerRemote();
            Naming.rebind("serverRemote", serverRemote);

            System.out.println("Server ready, launching event...");
            
            // launch event
            serverRemote.startEvent("Events/test.txt");
            
            System.out.println("Server on standby.");
            
        } catch (MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
