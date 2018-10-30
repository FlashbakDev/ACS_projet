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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
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
    
    private TextArea textArea_eventMessages;
    
    public ScenesManager(Client controller, Stage stage) {
    
        this.controller = controller;
        this.stage = stage;
        
        scenes = new HashMap<>();
        buildScenes();
    }
    
    public void switchScene(SceneTypes scene){
   
        stage.setTitle(scene.toString());
        stage.setScene(scenes.get(scene));

        if(scene == SceneTypes.CONNECTION)
            stage.setResizable(false);
        else
            stage.setResizable(true);
        
        stage.show();
    }
    
    private void buildScenes(){
        
        buildConnectionScene();
        buildEventScene();
    }
    
    public void addMessage(String message){
        
        textArea_eventMessages.textProperty().setValue(textArea_eventMessages.textProperty().getValue() + message + "\n");
    }
    
    private void buildConnectionScene(){
        
        // main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        // 3 rows
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        
        // 2 columns
        grid.getColumnConstraints().add(new ColumnConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints());
        
        // content
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
        
        // adds scene
        scenes.put(SceneTypes.CONNECTION, new Scene(grid, 350, 150));
    }
    
    private void buildEventScene(){
        
        // main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        
        // 3 rows
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
        
        grid.getRowConstraints().get(1).setVgrow(Priority.ALWAYS);
        
        // 1 columns
        grid.getColumnConstraints().add(new ColumnConstraints());
        
        grid.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
        
        // Content
        Text text_title = new Text("Suivie d'évènement");
        text_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(text_title, 0, 0);

        textArea_eventMessages = new TextArea();
        textArea_eventMessages.setEditable(false);
        grid.add(textArea_eventMessages, 0, 1);
        
        Button button_disconnect = new Button();
        button_disconnect.setText("Déconnexion");
        button_disconnect.setOnAction((ActionEvent event) -> { 
            controller.onClickOnButton_disconnect();
        });
        button_disconnect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_disconnect, 0, 2);

        // add scene
        scenes.put(SceneTypes.EVENTS, new Scene(grid, 800, 600));
    }
}
