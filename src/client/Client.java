package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import rmi.Bet;
import rmi.ClientListener;
import rmi.IServerRemote;
import rmi.Player;

/**
 * Classe qui gere les communications avec le rmi
 * Crée et manage la vue
 * Reçoit les evenements de l'ihm ou du serveur.
 * @author Benjamin
 */
public class Client extends Application {

    private IServerRemote serverRemote;
    private ClientListener clientRemote;
    
    private ScenesManager view;
    private long id;

    @Override
    public void start(Stage primaryStage) {

        id = -1;
        view = new ScenesManager(this, primaryStage);
        view.switchScene(ScenesManager.SceneTypes.CONNECTION);
    }

    /** 
     * Initialise la connexion rmi puis Change la vue
     *
     * @param ipAdress : Adresse ip du serveur distant
     */
    public void onClickOnButton_connect(String ipAdress) {

        String url = "rmi://" + ipAdress + "/serverRemote";

        try {

            serverRemote = (IServerRemote) Naming.lookup(url);
            clientRemote = new ClientListener(this);
            id = serverRemote.connect(clientRemote);
            
            if(id < 0){
                
                UnicastRemoteObject.unexportObject(clientRemote, true);
            }
            
            view.switchScene(ScenesManager.SceneTypes.EVENTS);

            List<String> history = this.serverRemote.getEventHistory(id);

            history.forEach((passedline) -> {
                this.view.addMessage(passedline); 
            });

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            view.switchScene(ScenesManager.SceneTypes.CONNECTION);
        }
    }

    /**
     * Ferme la connexion 
     *      
     */
    public void onClickOnButton_disconnect() {

        try {

            serverRemote.disconnect(id);
            
            try{
                
                UnicastRemoteObject.unexportObject(clientRemote, true);
                //UnicastRemoteObject.unexportObject(serverRemote, true);
                
            }catch(NoSuchObjectException ex){
                
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
            }
            
            id = 0;
            view.switchScene(ScenesManager.SceneTypes.CONNECTION);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Affiche tous ce qui est envoyer par le serveur
     * @param message : objet à afficher dans l'ihm
     */
    public void onEventMessageReceived(String message) {

        this.view.addMessage(message);
    }

    /**
     * Action activivé pas le listener pour 
     * mettre à jour l'indicateur de fin du match
     */
    public void onEventEnd() {
        
        this.view.EventEnd();
    }

    /** 
     * Action activé par les événements ihm
     * Envoie un vote au serveur
     * @param j : Le joueur pour qui l'utilisateur vote
     */
    public void onClickOnButton_vote(Player j) {

        try {

            serverRemote.vote(id, j);
            
        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Action activé par les événements ihm
     * Envoie un pari au serveur
     * @param b
     */
    public void onClickOnButton_bet(Bet b) {

        try {

            serverRemote.bet(id, b);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Include clean server disconnection.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        System.out.println("client.Client.stop()");

        if (id > 0) {

            serverRemote.disconnect(id);
            
            try{
                
                UnicastRemoteObject.unexportObject(clientRemote, true);
                //UnicastRemoteObject.unexportObject(serverRemote, true);
                
            }catch(NoSuchObjectException ex){
                
                //parfois les unexport leve une exception, des fois non. 
                //Mais sa marche jamais si on les met pas
                //Alors on capture et on se pose pas de question.
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Pointd'entrée du programme
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("Client start");
        launch(args);
    }

    /**
     *
     * @return La liste des joueurs, et le nombre de vote de chacun. 
     * 
     */
    public List<Player> getPlayersList() {
        
        try {

            return serverRemote.getPlayersList(id);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @return La liste des resultats pariables
     */
    public List<Bet> getPariList() {

        try {

            return serverRemote.getAvailableBets(id);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return Recupere une liste de toutes les lignes envoyé par le serveur. 
     * (La liste est celle du serveur, c'est independant de ce que le client 
     * a obtenu. Ne comprend pas les messages individuel (Pari gagnant etc ... )
     */
    public List<String> getPassedLines() {

        try {
            
            return this.serverRemote.getEventHistory(id);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
