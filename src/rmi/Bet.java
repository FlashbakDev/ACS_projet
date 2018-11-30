/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.Serializable;

/**
 * Identifie un pari valide
 * @author Benjamin
 */
public class Bet implements Serializable{
    
    private String name;

    public Bet(String name) {
        
        this.name = name;
    }
    
    @Override
    public String toString(){
        
        return this.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if(!(obj instanceof Bet))
            return false;
        
        return this.name.equals(((Bet)(obj)).name);
    }
    
    @Override
    public int hashCode() {
        
        return this.name.hashCode();
    }
}
