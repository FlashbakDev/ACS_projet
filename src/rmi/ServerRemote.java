/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benjamin
 */
public class ServerRemote extends UnicastRemoteObject implements IServerRemote {

    private EventsManager eventsManager;
    private final Map<Long, ClientInst> clients;
    private long ids;

    public ServerRemote() throws RemoteException {
        super();

        eventsManager = new EventsManager(this, "Events/test.txt");
        clients = new HashMap<>();
        ids = 0;

        eventsManager.start();
    }

    @Override
    public String test() throws RemoteException {

        try {

            String ip = getClientHost();
            System.out.println("[" + ip + "] ServerRemote.test()");

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Hello world !";
    }

    @Override
    public long connect(IEventMessagesListener listener) throws RemoteException {

        try {

            String ip = getClientHost();

            System.out.println("[" + ip + "] ServerRemote.connect()");

            // already exists
            for (Map.Entry<Long, ClientInst> entry : clients.entrySet()) {

                if (entry.getValue().getIp().equals(ip)) {

                    if (!entry.getValue().connected) {

                        System.out.println("[" + ip + "] reconnected");

                        entry.getValue().connected = true;
                        entry.getValue().listener = listener;

                        List<String> lines = eventsManager.getPassedLines();
                        for (String line : lines) {
                            entry.getValue().listener.EventMessageReceived(line);
                        }

                        return entry.getValue().getId();
                    }
                }
            }

            // add new
            ClientInst client = new ClientInst(getNextId(), ip, listener);
            clients.put(client.getId(), client);

            System.out.println("[" + ip + "] connected");

            return client.getId();

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    @Override
    public boolean disconnect(long id) throws RemoteException {

        try {

            String ip = getClientHost();

            System.out.println("[" + ip + "] ServerRemote.disconnect()");

            if (clients.containsKey(id)) {

                clients.get(id).connected = false;
                clients.get(id).listener = null;

                System.out.println("[" + ip + "] disconnected");
                return true;
            }

        } catch (ServerNotActiveException ex) {

            Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    //peut etre synchronized ?
    private long getNextId() {

        ids++;
        return ids;
    }

    public void notifyListeners(String message) {

        System.out.println("ServerRemote.notifyListeners() : " + message);

        clients.entrySet().forEach((entry) -> {

            if (entry.getValue().connected) {

                try {

                    entry.getValue().listener.EventMessageReceived(message);

                } catch (RemoteException ex) {

                    Logger.getLogger(ServerRemote.class.getName()).log(Level.SEVERE, null, ex);

                    // client is disconnected
                    entry.getValue().connected = false;
                    entry.getValue().listener = null;
                }
            }
        });
    }
}
