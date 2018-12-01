package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 * @author Benjamin
 */
public interface IClientListener extends Remote {

    public void EventMessage(String message) throws RemoteException;
    public void EventEnd(Bet clientBet, Bet result, Map<Player, Integer> votes) throws RemoteException;
    public void Kick(String message) throws RemoteException;
}
