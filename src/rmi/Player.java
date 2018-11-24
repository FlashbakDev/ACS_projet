/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.Serializable;

/**
 *
 * @author jerem
 * Identifie un joueur
 * @version 1.0
 */
public class Player implements Serializable{
    /**Le nom du joueur, sert egalement à l'indentifier
     * @since 1.0     
     */
    private String nom;
    
    /**Constructeur
     * @param nom : Le nom du joueur
     * @since 1.0
     */
    public Player(String nom){
        this.nom = nom;
    }
    
    @Override
    /**Affiche le nom du joueur
     * @return : Le nom du joueur
     * @since 1.0
     */
    public String toString(){
        return this.nom;
        
    }
    
    @Override
    /*On redefini le equals pour utiliser les Set<>*/
    /**
     * 
     * Compare le nom du Joueur
     * @since 1.0
     */
    public boolean equals(Object obj) {
        
        if(!(obj instanceof Player))
            return false;
        
        return this.nom.equals(((Player)(obj)).nom);
    }
    
    @Override
    //On redefini pour le equals
    public int hashCode() {
        
        return this.nom.hashCode();
    }
}
