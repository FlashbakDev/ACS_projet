/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import rmi.IContract;

/**
 *
 * @author Benjamin
 */
public class Client extends Application {
    
    private IContract contract; 
    private ScenesManager scenesManager;

    @Override
    public void start(Stage primaryStage) {

        scenesManager = new ScenesManager(this, primaryStage);
        scenesManager.switchScene(ScenesManager.SceneTypes.CONNECTION);
    }
    
    public void onClickOnButton_connect(String ipAdress){
        
        String url = "rmi://" + ipAdress + "/contract";

        try {
            
            contract = (IContract) Naming.lookup(url);
            contract.connect();
            
            scenesManager.switchScene(ScenesManager.SceneTypes.EVENTS);
            
        } catch (MalformedURLException | NotBoundException | RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void onClickOnButton_disconnect(){
        
        try {
            contract.disconnect();
            
            scenesManager.switchScene(ScenesManager.SceneTypes.CONNECTION);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Client start");
        
        launch(args);
    }
}
