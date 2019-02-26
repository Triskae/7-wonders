package commun.plateaux;

import java.util.ArrayList;

public class LaGrandePyramideDeGizeh extends Plateau {

  public LaGrandePyramideDeGizeh() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("5 points");
    etapes.add("7 points");

    super.setNom("La Grande Pyramide de Gizeh");
    super.setRessource(RessourceDepart.PIERRE);
    super.setEtapes(etapes);
  }

  public String toString() {
   return super.toString();
  }
}
