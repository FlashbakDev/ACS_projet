package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
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

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Ferme la connexion */
    public void onClickOnButton_disconnect() {

        try {

            serverRemote.disconnect(id);
            UnicastRemoteObject.unexportObject(clientRemote, true);
            id = 0;

            view.switchScene(ScenesManager.SceneTypes.CONNECTION);

        } catch (RemoteException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onMessageReceived(String message) {

        view.addMessage(message);
    }
    
    /** Valide le vote */
    public void onClickOnButton_vote(Player j) {

        try {

            serverRemote.vote(id, j);

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
        super.stop(); //To change body of generated methods, choose Tools | Templates.

        if (id > 0) {

            serverRemote.disconnect(id);
            UnicastRemoteObject.unexportObject(clientRemote, true);
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
     * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
     *
     * @return the salt for hashing
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return generatedPassword;
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
}
