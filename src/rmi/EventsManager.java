package rmi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benjamin
 * @version 1.2
 */
public class EventsManager extends Thread {

    private static final float timeSpeed = 1.0f; // 1 min = 1 seconde
    private final ServerRemote serverRemote;
    private List<String> lines;
    private List<String> passedLines;
    private Map<Player, Integer> playersVotes;
    boolean match_en_cour = false;
    private Set<String> pari_equipe;

    /**
     * @param contract
     * @param fileName
     * @since 1.0
     */
    public EventsManager(ServerRemote contract, String fileName) {

        this.serverRemote = contract;
        lines = new ArrayList<>();
        passedLines = new ArrayList<>();

        this.playersVotes = new HashMap<Player, Integer>();
        this.pari_equipe = new HashSet<String>();

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

        String ligne = itr.previous();
        this.initPariPossible(ligne);
        ligne = itr.previous();
        this.initListJoueur(ligne); //La premiere ligne est envoyé à l'initialisateur de joueurs
        this.match_en_cour = true;
        while (itr.hasPrevious()) {

            try {
                ligne = itr.previous();

                //Supprime un eventuelle premier caractere d'UTF. Necessaire pour la premiere ligne du fichier.
                if (Character.isIdentifierIgnorable(ligne.charAt(0))) {
                    ligne = ligne.substring(1);
                }

                int nextTime = Integer.parseInt(ligne.split(" ")[0]);
                int waitTime = nextTime - time;

                ligne = ligne.substring(ligne.split(" ")[0].length() + 1);
                ligne = "[" + nextTime + ":00]" + ligne;
                //System.out.println("time = "+ time +", next time = "+ nextTime + ", waitTime = "+ waitTime +", "+ (long)((waitTime*60000)*timeSpeed));
                sleep((long) ((waitTime * 1000) * timeSpeed));

                time += waitTime;
                serverRemote.notifyListeners(ligne);
                passedLines.add(ligne);

            } catch (InterruptedException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        this.match_en_cour = false;
        serverRemote.finDuMatch();
        ligne = "Le(s) joueur(s) ayant obtenu le plus de vote est/sont : " + this.getName_joueur_voté();
        serverRemote.notifyListeners(ligne);
        passedLines.add(ligne);

    }

    /**
     * @param s : String contenant ':' suivit d'une liste de nom séparé par des
     *          ','
     * @since 1.1
     */
    public void initListJoueur(String s) {

        String[] names_joueurs = s.split(":")[1].split(","); //Les noms des joueurs commencent au : et sont separé par une virgule.

        for (String names_joueur : names_joueurs) {
            playersVotes.put(new Player(names_joueur), 0);
        }
    }

    /**
     * @since 1.1
     */
    public void initPariPossible(String s) {
        String[] names_resultat = s.split(":")[1].split(","); //Les noms des joueurs commencent au : et sont separé par une virgule.

        for (String name_resultat : names_resultat) {
            pari_equipe.add(name_resultat);
        }
    }

    /**
     * @since 1.2
     */
    private Map<Player, Integer> getName_joueur_voté() {

        Map.Entry<Player, Integer> maxEntry = null;
        Map<Player, Integer> liste_j = new HashMap();

        for (Map.Entry<Player, Integer> entry : playersVotes.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
                liste_j.clear();
                liste_j.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue().compareTo(maxEntry.getValue()) == 0) {
                liste_j.put(entry.getKey(), entry.getValue());
            }
        }

        return liste_j;

    }

    /**
     * @since 1.0
     */
    public List<String> getPassedLines() {
        return passedLines;
    }

    public boolean vote(Player j) {

        if (j != null && this.match_en_cour) {

            if (this.playersVotes.containsKey(j)) {

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j) + 1);
                return true;
            }
        }

        return false;
    }

    /**
     * Supprime le vote pour un joueur
     */
    public boolean unvote(Player j) {

        if (j != null && this.match_en_cour) {

            if (this.playersVotes.containsKey(j)) {

                this.playersVotes.replace(j, this.playersVotes.get(j), this.playersVotes.get(j) - 1);
                return true;
            }
        }

        return false;
    }

    public Map<Player, Integer> getPlayersVotes() {
        return this.playersVotes;
    }

    public Set<String> getPari() {
        return this.pari_equipe;
    }

    public boolean getMatch_en_cour() {
        return this.match_en_cour;
    }
;
}
