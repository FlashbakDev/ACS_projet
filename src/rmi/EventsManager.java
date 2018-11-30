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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Gere les evenements (lecture du fichier d'evenement)
 * + Envoie des evenements aux clients.
 * Sert aussi pour l'enregistrement des decisions de clients.
 * @author Benjamin
 * @version 1.2
 */
public class EventsManager extends Thread {

    /**Le gestionnaire de vitesse pour les evenements*/
    private static final float TIMESPEED = 1.0f; // 1 min = 1 seconde
    
    /**L'instance RMI*/
    private final ServerRemote serverRemote;
    
    /**Une liste de toute les lignes de texte du fichier d'evenement*/
    private List<String> lines;
    
    /**Une liste de tout les messages envoyé aux clients*/
    private List<String> history;
    
    /**La liste des joueurs ainsi que leur nombre de votes*/
    private Map<Player, Integer> votes;
    
    /**La liste des options de pari*/
    private List<Bet> validBets;
    
    /**L'option gagnante des pari*/
    private Bet result;
    
    /**Indique si un match est en cour ou non*/
    private boolean isEventRuning;

    /**
     * Constructeur, il initialise les attributs, et recupere l'intégralité 
     * du fichier texte. (On pourrait surement economiser de la Ram en lisant le
     * fichier à la volé ... )
     * @param contract : L'instance rmi
     * @since 1.0
     */
    public EventsManager(ServerRemote contract){

        this.serverRemote = contract;
        resetEvent();
    }
    
    private void resetEvent(){
        
        lines = new ArrayList<>();
        history = new ArrayList<>();
        this.votes = new HashMap<>();
        this.validBets = new ArrayList<>();
        
        result = null;
        isEventRuning = false;
    }

    public void startEvent(String fileName) throws IOException{
        
        resetEvent();
        lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        
        ListIterator<String> itr = lines.listIterator();
        
        this.initValidBets(itr.next());
        this.initPlayers(itr.next()); 
        
        start();
    }

    /**
     * Les noms des paris commencent au : et sont separé par une virgule.
     * @param s
     * @since 1.1
     */
    public void initValidBets(String s) {
        
        String[] bets = s.split(":")[1].split(",");

        for (String bet : bets) {
            
            validBets.add(new Bet(bet));
        }
        
        result = validBets.get(0);
    }
    
    /**
     * Les noms des joueurs commencent au : et sont separé par une virgule.
     * @param s : String contenant ':' suivit d'une liste de nom séparé par des
     *          ','
     * @since 1.1
     */
    public void initPlayers(String s) {

        String[] names = s.split(":")[1].split(",");

        for (String name : names) {
            votes.put(new Player(name), 0);
        }
    }
    
    /**
     * @since 1.0
     */
    @Override
    public void run() {

        int time = 0;
        ListIterator<String> itr = lines.listIterator();
        this.isEventRuning = true;
        
        while (itr.hasNext()) {

            try {
                
                String line = itr.next();

                //Supprime un eventuelle premier caractere d'UTF.
                if (Character.isIdentifierIgnorable(line.charAt(0))) {
                    
                    line = line.substring(1);
                }

                int nextTime = Integer.parseInt(line.split(" ")[0]);
                int waitTime = nextTime - time;

                line = line.substring(line.split(" ")[0].length() + 1);
                line = "[" + nextTime + ":00]" + line;
                
                sleep((long) ((waitTime * 1000) * TIMESPEED));

                time += waitTime;
                
                serverRemote.sendEventMessage(line);
                lines.remove(line);
                history.add(line);

            } catch (InterruptedException | NumberFormatException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        serverRemote.sendEventEnded(this.result, votes);
        this.isEventRuning = false;
    }

    /**
     * @return 
     * @since 1.0
     */
    public List<String> getHistory() { return history; }

    public boolean vote(Player j) {

        if (j != null && this.isEventRuning) {

            if (this.votes.containsKey(j)) {

                this.votes.replace(j, this.votes.get(j), this.votes.get(j) + 1);
                return true;
            }
        }

        return false;
    }

    /**
     * Supprime le vote pour un joueur
     * @param j
     * @return 
     */
    public boolean unvote(Player j) {

        if (j != null && this.isEventRuning) {

            if (this.votes.containsKey(j)) {

                this.votes.replace(j, this.votes.get(j), this.votes.get(j) - 1);
                return true;
            }
        }

        return false;
    }

    public Map<Player, Integer> getPlayersVotes() { return this.votes;}

    public List<Bet> getAvailableBets() {return this.validBets;}

    public boolean getIsEventRunning() {return this.isEventRuning;}
}
