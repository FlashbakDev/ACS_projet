/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.HashMap;
import java.util.Map;
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

/**
 *
 * @author Benjamin
 */
public class ScenesManager {

    public enum SceneTypes{
        
        CONNECTION,
        EVENTS
    }
    
    private Client controller;
    private Stage stage;
    
    private final Map<SceneTypes, Scene> scenes;
    
    public ScenesManager(Client controller, Stage stage) {
    
        this.controller = controller;
        this.stage = stage;
        
        scenes = new HashMap<>();
        buildScenes();
    }
    
    public void switchScene(SceneTypes scene){
   
        stage.setTitle(scene.toString());
        stage.setScene(scenes.get(scene));
        stage.show();
    }
    
    private void buildScenes(){
        
        buildConnectionScene();
        buildEventScene();
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
        textField_ipAdress.textProperty().setValue("127.0.0.1");
        grid.add(textField_ipAdress,1,1);
        
        Button button_connect = new Button();
        button_connect.setText("Connexion");
        button_connect.setOnAction((ActionEvent event) -> { 
            controller.onClickOnButton_connect(textField_ipAdress.textProperty().getValue());
        });
        button_connect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_connect, 0,2,2,1);
        
        StackPane root = new StackPane();
        root.getChildren().add(grid);
        
        scenes.put(SceneTypes.CONNECTION, new Scene(root, 350, 150));
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
            controller.onClickOnButton_disconnect();
        });
        button_disconnect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_disconnect, 0, 1);        

        StackPane root = new StackPane();
        root.getChildren().add(grid);
        
        scenes.put(SceneTypes.EVENTS, new Scene(root, 800, 600));
    }
}
