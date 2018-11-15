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
    private String pseudo;
    private int password;
    public boolean connected;
    public IEventMessagesListener listener;

    public ClientInst(long id, String ip, IEventMessagesListener listener, String pseudo, int mdp) {
    
        this.id = id;
        this.ip = ip;
        this.listener = listener;
        this.password = mdp;
        this.pseudo = pseudo;
        connected = true;
    }
    public Boolean isPass(int mdp){
        return mdp == password;
    }

    public String getIp(){return ip;}
    public long getId(){return id;}
    public String getpseudo(){return pseudo;}
}
