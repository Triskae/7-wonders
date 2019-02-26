package commun.plateaux;

import java.util.ArrayList;

public abstract class Plateau {

  private String nom;
  private RessourceDepart ressourceDepart;
  private ArrayList<String> etapes;

  public String getNom() { return this.nom; }

  public RessourceDepart getRessource() {
    return this.ressourceDepart;
  }

  public ArrayList<String> getEtapes() {
    return this.etapes;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public void setRessource(RessourceDepart ressourceDepart) {
    this.ressourceDepart = ressourceDepart;
  }

  public void setEtapes(ArrayList<String> etapes) {
    this.etapes = etapes;
  }

  public String toString() {
   return this.nom + " : < " + this.ressourceDepart + ", " + this.etapes + " > ";
  }

}
