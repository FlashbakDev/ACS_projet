package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Benjamin
 */
public interface IServerRemote extends Remote {

    public String test() throws RemoteException;

    public long connect(IEventMessagesListener listener) throws RemoteException;

    public boolean disconnect(long id) throws RemoteException;

    public List<Joueur> getListJoueurs();
}
