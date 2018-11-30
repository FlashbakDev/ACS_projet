package client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import rmi.Bet;
import rmi.Player;

/**
 *
 * @author Benjamin
 * @version 1.0
 */
public class ScenesManager {

    private boolean finduMatch;

    public enum SceneTypes {

        CONNECTION,
        EVENTS
    }

    private final Client controller;
    private final Stage stage;
    private final Map<SceneTypes, Scene> scenes;
    private TextArea textArea_messages;
    private ChoiceBox<Player> choiceBox_player;
    private ChoiceBox<Bet> choiceBox_bet;

    private Button button_vote;
    private Button button_bet;
    
    /**
     * Utile plus tard pour faire un clean plus propre de la scene précédente
     * lors d'un switch
     *
     * @since 1.0
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
     * @since 1.0
     */
    public void switchScene(SceneTypes sceneType) {

        stage.setTitle(sceneType.toString());
        stage.setScene(scenes.get(sceneType));
        
        if (sceneType == SceneTypes.EVENTS) {
            
            stage.setResizable(true);
            
            textArea_messages.clear();
            
            List<Player> players = controller.getPlayersList();
            if (players != null && players.size() > 0) {
                choiceBox_player.setItems(FXCollections.observableList(players));
            }

            List<Bet> bet = controller.getPariList();
            if (bet != null && bet.size() > 0) {
                choiceBox_bet.setItems(FXCollections.observableList(bet));
            }
        }
        else{
            
            stage.setResizable(false);
        }

        this.sceneType = sceneType;
        stage.show();
    }

    /**
     * Build all scenes.
     *
     * @since 1.0
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
     * @since 1.0
     */
    synchronized public void addMessage(String message) {

        try{
        
            ScrollBar scrollBar = (ScrollBar) textArea_messages.lookup(".scroll-bar:vertical");

            if( scrollBar != null ){

                if (scrollBar.valueProperty().get() == 1.0) {

                    textArea_messages.appendText(message + "\n");
                    scrollBar.setValue(1.0);

                } else {

                    int caretPosition = textArea_messages.caretPositionProperty().get();
                    textArea_messages.appendText(message + "\n");
                    textArea_messages.positionCaret(caretPosition);
                }
            }
            else{

                textArea_messages.appendText(message + "\n");
            }
        
        }
        catch(Exception ex){
            
            System.out.println("client.ScenesManager.addMessage() : exception = "+ ex);
        }
    }

    /**
     * Build the Connectioin scene.
     *
     * @since 1.0
     */
    private void buildConnectionScene() {

        // main layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 50, 25));

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
        grid.add(label_ipAdress, 0, 1);

        TextField textField_ipAdress = new TextField();
        textField_ipAdress.textProperty().setValue("127.0.0.1");
        grid.add(textField_ipAdress, 1, 1);

        Button button_connect = new Button();
        button_connect.setText("Connexion");
        button_connect.setOnAction((ActionEvent event) -> {
            controller.onClickOnButton_connect(textField_ipAdress.textProperty().getValue());
        });
        button_connect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_connect, 0, 2, 2, 1);

        // adds scene
        scenes.put(SceneTypes.CONNECTION, new Scene(grid, 350, 150));
    }

    /**
     * Build the Event scene.
     *
     * @since 1.0
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

        // 2 columns
        grid.getColumnConstraints().add(new ColumnConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints());

        grid.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);

        // grid Content
        Text text_title = new Text("Suivie d'évènement");
        text_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(text_title, 0, 0);

        textArea_messages = new TextArea();
        textArea_messages.setEditable(false);
        textArea_messages.setWrapText(true);
        grid.add(textArea_messages, 0, 1);

        Button button_disconnect = new Button();
        button_disconnect.setText("Déconnexion");
        button_disconnect.setOnAction((ActionEvent event) -> {
            controller.onClickOnButton_disconnect();
        });
        button_disconnect.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(button_disconnect, 0, 2);

        GridPane grid_right = new GridPane();
        grid_right.setAlignment(Pos.CENTER);
        grid_right.setHgap(10);
        grid_right.setVgap(10);
        grid_right.setPadding(new Insets(25, 25, 25, 25));
        grid_right.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(grid_right, 1, 0, 1, 2);

        // 2 collumns
        grid_right.getColumnConstraints().add(new ColumnConstraints());
        grid_right.getColumnConstraints().add(new ColumnConstraints());

        grid_right.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
        grid_right.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);

        // 3 rows
        grid_right.getRowConstraints().add(new RowConstraints());
        grid_right.getRowConstraints().add(new RowConstraints());
        grid_right.getRowConstraints().add(new RowConstraints());

        // grid_right content
        choiceBox_player = new ChoiceBox<>();
        choiceBox_player.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid_right.add(choiceBox_player, 0, 1);

        button_vote = new Button();
        button_vote.setText("Voter");
        button_vote.setOnAction((ActionEvent event) -> {

            controller.onClickOnButton_vote(choiceBox_player.getValue());
        });
        button_vote.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid_right.add(button_vote, 1, 1);

        // grid_right content
        choiceBox_bet = new ChoiceBox<>();
        choiceBox_bet.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid_right.add(choiceBox_bet, 0, 2);

        button_bet = new Button();
        button_bet.setText("Parier");
        button_bet.setOnAction((ActionEvent event) -> {
            
            controller.onClickOnButton_bet(choiceBox_bet.getValue());
        });
        button_bet.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid_right.add(button_bet, 1, 2);

        // add scene
        scenes.put(SceneTypes.EVENTS, new Scene(grid, 950, 600));
    }

    public void EventEnd() {
        
        System.out.println("client.ScenesManager.EventEnd()");
    }
}
