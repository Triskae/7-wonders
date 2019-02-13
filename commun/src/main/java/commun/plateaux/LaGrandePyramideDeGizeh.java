package commun.plateaux;

import java.util.ArrayList;

class LaGrandePyramideDeGizeh extends Plateau {

  LaGrandePyramideDeGizeh() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("5 points");
    etapes.add("7 points");

    super.setRessource(RessourceDepart.PIERRE);
    super.setEtapes(etapes);
  }

  public String toString() {
   return "LaGrandePyramideDeGizeh : < " + super.toString() + " >";
  }
}
