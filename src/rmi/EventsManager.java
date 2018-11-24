package rmi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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
    private Map<Player, Integer> playersVotes;
    
    /**
     * @param contract
     * @param fileName
     * @since 1.0
     */
    public EventsManager(ServerRemote contract, String fileName) {

        this.serverRemote = contract;
        lines = new ArrayList<>();
        passedLines = new ArrayList<>();

        initListJoueur();
        
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
     * @since 1.1
     */
    public void initListJoueur(){
        
        this.playersVotes = new HashMap<>();
        
        playersVotes.put(new Player("Michel"), 0);
        playersVotes.put(new Player("Jean"), 0);
    }

    /**
     * @since 1.0
     */
    public List<String> getPassedLines() {
        return passedLines;
    }
    
    public boolean vote(Player j){
        
        if(j != null){
        
            if(this.playersVotes.containsKey(j)){

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j)+1);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean unvote(Player j){
        
        if(j != null){
        
            if(this.playersVotes.containsKey(j)){

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j)-1);
                return true;
            }
        }
        
        return false;
    }
    
    public Map<Player, Integer> getPlayersVotes(){return this.playersVotes;}
}
