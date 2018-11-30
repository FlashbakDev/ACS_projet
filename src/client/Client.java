package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
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
    private ScenesManager view;
    private ClientListener clientRemote;
    private long id;

    @Override
    public void start(Stage primaryStage) {

        id = 0;
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
            
            if(id <0){
                
                UnicastRemoteObject.unexportObject(clientRemote, true);
                throw new NotBoundException("Erreur à la connexion");
            }
            
            view.switchScene(ScenesManager.SceneTypes.EVENTS);

            List<String> passedlines = this.serverRemote.getEventHistory();

            passedlines.forEach((passedline) -> {
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
            //UnicastRemoteObject.unexportObject(clientRemote, true);
            try{
                
                UnicastRemoteObject.unexportObject(clientRemote, true);
                UnicastRemoteObject.unexportObject(serverRemote, true);
                
            }catch(java.rmi.NoSuchObjectException no){
                
                System.err.println("erreur de deconnexion"); 
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
    public void onMessageReceived(String message) {

        this.view.addMessage(message);
    }

    /**
     * Action activivé pas le listener pour 
     * mettre à jour l'indicateur de fin du match
     */
    public void onFinDuMatch() {
        this.view.finduMatch();
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
     * @param j : Le resultat sur lequel l'utilisateur pari
     */
    public void onClickOnButton_pari(String j) {

        try {

            serverRemote.bet(id, j);

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
                UnicastRemoteObject.unexportObject(serverRemote, true);
                
            }catch(java.rmi.NoSuchObjectException no){
                
                //parfois les unexport leve une exception, des fois non. 
                //Mais sa marche jamais si on les met pas
                //Alors on capture et on se pose pas de question.
                System.err.println("erreur de stop");
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

            return serverRemote.getPlayersList();

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @return La liste des resultats pariables
     */
    public List<String> getPariList() {

        try {

            return serverRemote.getAvailableBets();

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
            
            return this.serverRemote.getEventHistory();
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
