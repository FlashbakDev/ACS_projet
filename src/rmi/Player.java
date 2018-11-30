/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.Serializable;

/**
 * Identifie un joueur
 * @author jerem
 * @version 1.0
 */
public class Player implements Serializable{
    
    /**
     * Le nom du joueur, sert egalement à l'indentifier
     * @since 1.0     
     */
    private String name;
    
    /**
     * Constructeur
     * @param nom : Le nom du joueur
     * @since 1.0
     */
    public Player(String nom){
        
        this.name = nom;
    }
    
    @Override
    /**
     * Affiche le nom du joueur
     * @return : Le nom du joueur
     * @since 1.0
     */
    public String toString(){
        
        return this.name;
    }
    
    @Override
    /**
     * On redefini le equals pour utiliser les Set<>
     * @since 1.0
     */
    public boolean equals(Object obj) {
        
        if(!(obj instanceof Player))
            return false;
        
        return this.name.equals(((Player)(obj)).name);
    }
    
    @Override
    public int hashCode() {
        
        return this.name.hashCode();
    }
}
