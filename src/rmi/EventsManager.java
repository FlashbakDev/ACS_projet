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
 * @author Benjamin
 * @version 1.2
 */
public class EventsManager extends Thread {

    private static final float timeSpeed = 0.5f; // 1 min = 1 seconde
    private final ServerRemote serverRemote;
    private List<String> lines;
    private List<String> passedLines;
    private Map<Player, Integer> playersVotes;
    boolean match_en_cour = false;
    /**
     * @param contract
     * @param fileName
     * @since 1.0
     */
    public EventsManager(ServerRemote contract, String fileName) {

        this.serverRemote = contract;
        lines = new ArrayList<>();
        passedLines = new ArrayList<>();

        this.playersVotes = new HashMap<>();
        
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
        //int heurededepart =
        String ligne = itr.previous();
        initListJoueur(ligne); //La premiere ligne est envoyé à l'initialisateur de joueurs
        this.match_en_cour = true;
        while (itr.hasPrevious()) {

            try {
                ligne = itr.previous();
                
                //Supprime un eventuelle premier caractere d'UTF. Necessaire pour la premiere ligne du fichier.
                if ( Character.isIdentifierIgnorable(ligne.charAt(0)) ) {
                    ligne = ligne.substring(1);
                }
                
                int nextTime = Integer.parseInt(ligne.split(" ")[0]);
                int waitTime = nextTime - time;

                //System.out.println("time = "+ time +", next time = "+ nextTime + ", waitTime = "+ waitTime +", "+ (long)((waitTime*60000)*timeSpeed));
                sleep((long) ((waitTime * 1000) * timeSpeed));

                time += waitTime;
                serverRemote.notifyListeners(ligne);
                passedLines.add(ligne);

            } catch (InterruptedException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex){
                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        this.match_en_cour = false;
        Map.Entry<Player,Integer> meilleurs_joueur = this.getName_joueur_voté();
        ligne = "Le joueur ayant obtenu le plus de vote est : " + meilleurs_joueur + "("+ meilleurs_joueur.getValue() +")";
        serverRemote.notifyListeners(ligne);
        passedLines.add(ligne);
        
    }
    
    /**
     * @since 1.1
     */
    public void initListJoueur(String s){
        
        String[] names_joueurs = s.split(":")[1].split(","); //Les noms des joueurs commencent au : et sont separé par une virgule.
        
        for (String names_joueur : names_joueurs) {
            playersVotes.put(new Player(names_joueur), 0);
        }
        
    }
    
    /**
     * @since 1.2
     */
    private Map.Entry<Player,Integer> getName_joueur_voté(){
        
        Map.Entry<Player,Integer> maxEntry = null;
        
        for (Map.Entry<Player, Integer> entry : playersVotes.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
       
        return maxEntry;
        
        
    }

    /**
     * @since 1.0
     */
    public List<String> getPassedLines() {
        return passedLines;
    }
    
    public boolean vote(Player j){
        
        if(j != null && this.match_en_cour){
        
            if(this.playersVotes.containsKey(j)){

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j)+1);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean unvote(Player j){
        
        if(j != null && this.match_en_cour){
        
            if(this.playersVotes.containsKey(j)){

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j)-1);
                return true;
            }
        }
        
        return false;
    }
    
    public Map<Player, Integer> getPlayersVotes(){return this.playersVotes;}
}
