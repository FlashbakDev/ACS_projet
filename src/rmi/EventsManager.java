package rmi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benjamin
 * @version 1.1
 */
public class EventsManager extends Thread {

    private static final float timeSpeed = 1f; // 1 min = 1 seconde
    private final ServerRemote serverRemote;
    private List<String> lines;
    private List<String> passedLines;

    /**
     * @since 1.0
     */
    public EventsManager(ServerRemote contract, String fileName) {

        this.serverRemote = contract;
        lines = new ArrayList<>();
        passedLines = new ArrayList<>();

        try {

            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

        } catch (IOException ex) {

            Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @since 1.0
     */
    @Override
    public void run() {

        ListIterator<String> itr = lines.listIterator(lines.size());
        int time = 0;

        while (itr.hasPrevious()) {

            try {

                int nextTime = Integer.parseInt(itr.previous().split(" ")[0]);
                int waitTime = nextTime - time;

                //System.out.println("time = "+ time +", next time = "+ nextTime + ", waitTime = "+ waitTime +", "+ (long)((waitTime*60000)*timeSpeed));
                sleep((long) ((waitTime * 1000) * timeSpeed));

                time += waitTime;
                serverRemote.notifyListeners(itr.previous());
                passedLines.add(itr.previous());

            } catch (InterruptedException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Fournit au serveur la liste des joueurs disponibles.
     * @since 1.1
     */
    public List<Joueur> generateListJoueur(){
        
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     
    }

    /**
     * @since 1.0
     */
    public List<String> getPassedLines() {
        return passedLines;
    }
}
