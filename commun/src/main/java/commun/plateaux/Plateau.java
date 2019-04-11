package commun.plateaux;

import commun.cartes.Carte;

import java.util.ArrayList;

public abstract class Plateau {

  private String nom;
  private RessourceDepart ressourceDepart;
  private ArrayList<String> etapesA;
  private ArrayList<String> etapesB;
  private ArrayList<Carte> cartesJouees = new ArrayList<>();
  private char face;

  public String getNom() { return this.nom; }

  public RessourceDepart getRessource() {
    return this.ressourceDepart;
  }

  public ArrayList<String> getEtapes() {
    if(this.getFace() == 'A')
      return this.etapesA;
    else
      return this.etapesB;
  }

  public ArrayList<Carte> getCartesJouees() {
    return cartesJouees;
  }

  public void ajouterCarteJouee(Carte c) {
    cartesJouees.add(c);
  }

  public char getFace() {
    return this.face;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public void setRessource(RessourceDepart ressourceDepart) {
    this.ressourceDepart = ressourceDepart;
  }

  public void setEtapesA(ArrayList<String> etapesA) {
    this.etapesA = etapesA;
  }

  public void setEtapesB(ArrayList<String> etapesB) {
    this.etapesB = etapesB;
  }

  public void setFace(char face) { this.face = face; }

  public String toString() {
   return this.getNom() + " : < " + this.getRessource() + ", " + this.getEtapes() + " > ";
  }

}
