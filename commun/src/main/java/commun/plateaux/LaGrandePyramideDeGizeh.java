package commun.plateaux;

import java.util.ArrayList;

public class LaGrandePyramideDeGizeh extends Plateau {

  public LaGrandePyramideDeGizeh() {
    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("5 points");
    etapesA.add("7 points");


    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("3 points");
    etapesB.add("5 points");
    etapesB.add("5 points");
    etapesB.add("7 points");

    super.setNom("La Grande Pyramide de Gizeh");
    super.setRessource(RessourceDepart.PIERRE);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
   return super.toString();
  }
}
