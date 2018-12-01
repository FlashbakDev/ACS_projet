/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;
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
            
            System.out.println("Server on standby.");
            
            help();
            
            boolean quit = false;
            while(!quit){
                
                Scanner scanner = new Scanner(System.in);
                
                String line = scanner.nextLine();
                
                switch(line.split(" ")[0]){
                    
                    case "quit": quit = true; break;
                    case "help": help(); break;
                    case "start":{
                        
                        try{
                            
                            String file = line.split(" ")[1];
                            serverRemote.startEvent(file);
                        }
                        catch(ArrayIndexOutOfBoundsException ex){
                            
                            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("File do not exists.");
                        }
                    }
                    break;
                    
                    case "stop": serverRemote.stopEvent(); break;
                    
                    default:{
                        
                        System.out.println("command non reconnue");
                    }
                }
            }
            
            serverRemote.quit();
            System.exit(0);
            
        } catch (MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void help() {
        
        System.out.println(""
                + "quit : shut down the server.\n"
                + "help : display help.\n"
                + "start <filename> : start event from file.\n"
                + "stop : stop the running event if exists."
                );
    }
}
