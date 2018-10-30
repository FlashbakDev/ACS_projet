/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benjamin
 */
public class EventsManager extends Thread {

    private ServerRemote serverRemote;
    private File eventFile;
    
    public EventsManager(ServerRemote contract, File eventFile) {
        
        this.serverRemote = contract;
        this.eventFile = eventFile;
    }

    @Override
    public void run() {
        
        while (true) {

            try {

                sleep(1000);
                serverRemote.notifyListeners();

            } catch (InterruptedException ex) {

                Logger.getLogger(EventsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
