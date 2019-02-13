package commun.plateaux;

import java.util.ArrayList;

abstract class Plateau {

  private RessourceDepart ressourceDepart;
  private ArrayList<String> etapes;

  public RessourceDepart getRessource() {
    return this.ressourceDepart;
  }

  public ArrayList<String> getEtapes() {
    return this.etapes;
  }

  public void setRessource(RessourceDepart ressourceDepart) {
    this.ressourceDepart = ressourceDepart;
  }

  public void setEtapes(ArrayList<String> etapes) {
    this.etapes = etapes;
  }

  public String toString() {
   return this.ressourceDepart + ", " + this.etapes;
  }

}
