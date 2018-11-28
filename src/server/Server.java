/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    //Verifier que rmi est lancer
    /**
     * Initialise le serveur 
     * Peut provoquer des erreurs si le rmiregistry n'est pas actif
     * ou si le fichier d'evenement n'est pas valide
     */
    public void init() {

        System.out.println("Server start");

        System.out.println("Server init...");

        try {

            serverRemote = new ServerRemote();
            System.out.println("Object created.");

            Naming.rebind("serverRemote", serverRemote);
            System.out.println("Object saves.");

            System.out.println("Server ready.");

        } catch (MalformedURLException | RemoteException e) {
               //Erreur de rmi
            System.out.println(e);
        }catch (IOException ex) {
            //Fichier non valide
            Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        Server server = new Server();
        server.init();
    }
}
