/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

/**
 *
 * @author Benjamin
 */
public class ClientInst {
    
    private long id;
    private String ip;
    public boolean connected;
    public IMessageListener listener;

    public ClientInst(long id, String ip, IMessageListener listener) {
    
        this.id = id;
        this.ip = ip;
        this.listener = listener;
        
        connected = true;
    }

    public String getIp(){return ip;}
    public long getId(){return id;}
}
