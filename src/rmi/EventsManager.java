/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Path;

/**
 *
 * @author Benjamin
 */
public class EventsManager extends Thread {

    private final ServerRemote serverRemote;
    private List<String> lines;
    
    public EventsManager(ServerRemote contract, String fileName) {

        this.serverRemote = contract;
        lines = new ArrayList<>();
        
        try {
            
            lines = Files.readAllLines(Paths.get(fileName),  StandardCharsets.UTF_8);

        } catch (IOException ex) {
            
            Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        
        ListIterator <String> itr = lines.listIterator (lines.size());
        
        while (itr.hasPrevious()) {

            try {
                
                serverRemote.notifyListeners(itr.previous());
                sleep(1000);

            } catch (InterruptedException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
