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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import rmi.IContract;

/**
 *
 * @author Benjamin
 */
public class Client extends Application {
    
    private IContract contract;
    private StringProperty ipAdress;
    
    private Stage stage;
    
    private Scene scene_connexion;
    private Scene scene_event;

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        ipAdress = new SimpleStringProperty("127.0.0.1");
        
        buildConnectionScene();
        buildEventScene();
        
        stage.setTitle("Connexion");
        stage.setScene(scene_connexion);
        stage.show();
    }
    
    private void buildConnectionScene(){
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        Text text_title = new Text("Connexion");
        text_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(text_title, 0, 0, 2, 1);
        
        Label label_ipAdress = new Label("Adresse du serveur : ");
        grid.add(label_ipAdress,0,1);
        
        TextField textField_ipAdress = new TextField();
        textField_ipAdress.textProperty().bind(this.ipAdress);
        grid.add(textField_ipAdress,1,1);
        
        Button button_connect = new Button();
        button_connect.setText("Connexion");
        button_connect.setOnAction((ActionEvent event) -> { 
            connect();
            stage.setTitle("Suivie d'évènements");
            stage.setScene(scene_event);
            stage.show();
        });
        button_connect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_connect, 0,2,2,1);
        
        StackPane root = new StackPane();
        root.getChildren().add(grid);
        
        scene_connexion = new Scene(root, 350, 150);
    }
    
    private void buildEventScene(){
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        Text text_title = new Text("Suivie d'évènement");
        text_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(text_title, 0, 0);

        Button button_disconnect = new Button();
        button_disconnect.setText("Déconnexion");
        button_disconnect.setOnAction((ActionEvent event) -> { 
            disconnect();
            stage.setTitle("Connexion");
            stage.setScene(scene_connexion);
            stage.show();
        });
        button_disconnect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_disconnect, 0, 1);        

        StackPane root = new StackPane();
        root.getChildren().add(grid);
        
        scene_event = new Scene(root, 800, 600);
    }
    
    private void connect(){
        
        String url = "rmi://" + ipAdress.getValue() + "/contract";

        try {
            
            contract = (IContract) Naming.lookup(url);
            contract.connect();
            
        } catch (MalformedURLException | NotBoundException | RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void disconnect(){
        
        try {
            contract.disconnect();
            
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
