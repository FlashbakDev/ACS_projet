package rmi;

/**
 *
 * @author Benjamin
 */
public class ClientInst {

    private final long id;
    private final String ip;
    public boolean connected;
    public IEventMessagesListener listener;

    public ClientInst(long id, String ip, IEventMessagesListener listener) {

        this.id = id;
        this.ip = ip;
        this.listener = listener;

        connected = true;
    }

    public String getIp() {
        return ip;
    }

    public long getId() {
        return id;
    }
}
