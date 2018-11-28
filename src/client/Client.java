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
import rmi.EventMessagesListener;
import rmi.IServerRemote;
import rmi.Player;

//Verifier pourquoi la croix marche pas sur l'affichage du texte
/**
 *
 * @author Benjamin
 */
public class Client extends Application {

    private IServerRemote serverRemote;
    private ScenesManager view;
    private EventMessagesListener clientRemote;
    private long id;

    @Override
    public void start(Stage primaryStage) {

        id = 0;
        view = new ScenesManager(this, primaryStage);
        view.switchScene(ScenesManager.SceneTypes.CONNECTION);
    }

    /** Initialise la connexion rmi puis Change la vue
     *
     * @param ipAdress : Adresse ip du serveur distant
     */
    public void onClickOnButton_connect(String ipAdress) {

        String url = "rmi://" + ipAdress + "/serverRemote";

        try {

            serverRemote = (IServerRemote) Naming.lookup(url);
            clientRemote = new EventMessagesListener(this);
            id = serverRemote.connect(clientRemote);

            view.switchScene(ScenesManager.SceneTypes.EVENTS);

            List<String> passedlines = this.serverRemote.getPassedLines();

            for (String passedline : passedlines) {
                this.view.addMessage(passedline);
            }

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            view.switchScene(ScenesManager.SceneTypes.CONNECTION);
        }
    }

    /** Ferme la connexion */
    public void onClickOnButton_disconnect() {

        try {

            serverRemote.disconnect(id);
            //UnicastRemoteObject.unexportObject(clientRemote, true);
            try{
                UnicastRemoteObject.unexportObject(clientRemote, true);
                UnicastRemoteObject.unexportObject(serverRemote, true);
            }catch(java.rmi.NoSuchObjectException no){System.err.println("erreur de deconnexion"); }
            id = 0;

            view.switchScene(ScenesManager.SceneTypes.CONNECTION);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Affiche tous ce qui est envoyer par le serveur
     */
    public void onMessageReceived(String message) {

        this.view.addMessage(message);
    }

    public void onFinDuMatch() {
        this.view.finduMatch();
    }

    /** Valide le vote */
    public void onClickOnButton_vote(Player j) {

        try {

            serverRemote.vote(id, j);

           
        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Valide le pari */
    public void onClickOnButton_pari(String j) {

        try {

            serverRemote.pari(id, j);

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
            //UnicastRemoteObject.unexportObject(clientRemote, true);
            try{
                UnicastRemoteObject.unexportObject(clientRemote, true);
                UnicastRemoteObject.unexportObject(serverRemote, true);
            }catch(java.rmi.NoSuchObjectException no){
                System.err.println("erreur de stop");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("Client start");

        launch(args);

    }

    /**
     *
     * @return La liste des joueurs
     */
    public Map<Player, Integer> getPlayersList() {

        try {

            return serverRemote.getPlayersList();

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @return La liste des joueurs
     */
    public Set<String> getPariList() {

        try {

            return serverRemote.getPariList();

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<String> getPassedLines() {

        try {
            return this.serverRemote.getPassedLines();
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
