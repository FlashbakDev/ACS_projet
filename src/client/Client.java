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
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import rmi.EventMessagesListener;
import rmi.IServerRemote;
import rmi.IEventMessagesListener;

/**
 *
 * @author Benjamin
 */
public class Client extends Application{
    
    private IServerRemote serverRemote; 
    private ScenesManager view;
    private EventMessagesListener clientRemote;
    private List<String> eventMessages;
    private long id;

    @Override
    public void start(Stage primaryStage) {
        
        eventMessages = new ArrayList<>();
        id = 0;
        view = new ScenesManager(this, primaryStage);
        view.switchScene(ScenesManager.SceneTypes.CONNECTION);
    }
    
    public void onClickOnButton_connect(String ipAdress){
        
        String url = "rmi://" + ipAdress + "/serverRemote";

        try {

            serverRemote = (IServerRemote) Naming.lookup(url);
            clientRemote = new EventMessagesListener(this);
            id = serverRemote.connect(clientRemote);

            view.switchScene(ScenesManager.SceneTypes.EVENTS);
            
        } catch(MalformedURLException| NotBoundException| RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void onClickOnButton_disconnect(){
        
        try {
            
            serverRemote.disconnect(id);
            UnicastRemoteObject.unexportObject(clientRemote, true);
            
            view.switchScene(ScenesManager.SceneTypes.CONNECTION);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void onMessageReceived(String message){

        eventMessages.add(message);
        view.addMessage(message);
    }
    

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        
        onClickOnButton_disconnect();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Client start");
        
        launch(args);
    }
}
