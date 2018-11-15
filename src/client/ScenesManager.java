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
import javafx.scene.control.ScrollBar;
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

    public enum SceneTypes {

        CONNECTION,
        EVENTS
    }

    private Client controller;
    private Stage stage;
    private final Map<SceneTypes, Scene> scenes;
    private TextArea textArea_eventMessages;

    /**
     * Utile plus tard pour faire un clean plus propre de la scene précédente
     * lors d'un switch
     */
    private SceneTypes sceneType;

    public ScenesManager(Client controller, Stage stage) {

        this.controller = controller;

        this.stage = stage;

        scenes = new HashMap<>();

        buildScenes();
    }

    /**
     * Switch between scenes, if @param is SceneTypes.CONNECTION then stage
     * resisibily is set to false.
     *
     * @param sceneType scene to switch to
     */
    public void switchScene(SceneTypes sceneType) {

        //Eventuellement faire un rebuild de la scene
        if (sceneType == SceneTypes.EVENTS) {
            textArea_eventMessages.clear();
        }

        stage.setTitle(sceneType.toString());
        stage.setScene(scenes.get(sceneType));

        if (sceneType == SceneTypes.CONNECTION) {
            stage.setResizable(false);
        } else {
            stage.setResizable(true);
        }

        this.sceneType = sceneType;
        stage.show();
    }

    /**
     * Build all scenes.
     */
    private void buildScenes() {

        buildConnectionScene();
        buildEventScene();
    }

    /**
     * Append text to area and scroll to bottom only if vertical scrollbar was
     * at bottom of the area.
     *
     * @param message message to append
     */
    public void addMessage(String message) {

        ScrollBar scrollBar = (ScrollBar) textArea_eventMessages.lookup(".scroll-bar:vertical");
        if (scrollBar.valueProperty().get() == 1.0) {

            textArea_eventMessages.appendText(message + "\n");
        } else {

            int caretPosition = textArea_eventMessages.caretPositionProperty().get();
            textArea_eventMessages.appendText(message + "\n");
            textArea_eventMessages.positionCaret(caretPosition);
        }
    }

    /**
     * Build the Connectioin scene.
     */
    private void buildConnectionScene() {

        // main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 50, 25));

        // 5 rows
        grid.getRowConstraints().add(new RowConstraints());
        grid.getRowConstraints().add(new RowConstraints());
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
        grid.add(label_ipAdress, 0, 1);

        TextField textField_ipAdress = new TextField();
        textField_ipAdress.textProperty().setValue("127.0.0.1");
        grid.add(textField_ipAdress, 1, 1);
        
        Label label_pseudo = new Label("Pseudo : ");
        grid.add(label_pseudo, 0, 2);

        TextField textField_pseudo = new TextField();
        textField_pseudo.textProperty().setValue(" ");
        grid.add(textField_pseudo, 1, 2);
        
        Label label_mdp = new Label("Password : ");
        grid.add(label_mdp, 0, 3);

        TextField textField_mdp = new TextField();
        textField_mdp.textProperty().setValue(" ");
        grid.add(textField_mdp, 1, 3);

        Button button_connect = new Button();
        button_connect.setText("Connexion");
        button_connect.setOnAction((ActionEvent event) -> {
            controller.onClickOnButton_connect(textField_ipAdress.textProperty().getValue(), textField_pseudo.textProperty().getValue(), textField_mdp.textProperty().getValue());
        });
        button_connect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_connect, 0, 4, 2, 1);

        // adds scene
        scenes.put(SceneTypes.CONNECTION, new Scene(grid, 350, 150));
    }

    /**
     * Build the Event scene.
     */
    private void buildEventScene() {

        // main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

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
        textArea_eventMessages.setWrapText(true);
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
