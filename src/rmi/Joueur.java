/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

/**
 *
 * @author jerem
 * Identifie un joueur
 * @version 1.0
 */
public class Joueur {
    /**Le nom du joueur, sert egalement à l'indentifier
     * @since 1.0     
     */
    private String nom;
    
    /**Constructeur
     * @param nom : Le nom du joueur
     * @since 1.0
     */
    public Joueur(String nom){
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
      Joueur other=(Joueur) obj;
      return this.nom.equals(other.nom);
    }
    
    @Override
    //On redefini pour le equals
    public int hashCode() {
    return this.nom.hashCode();
    }
}
