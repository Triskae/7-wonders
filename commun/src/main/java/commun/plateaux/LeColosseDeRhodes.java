package commun.plateaux;

import java.util.ArrayList;

public class LeColosseDeRhodes extends Plateau {

  public LeColosseDeRhodes() {
    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("2 guerres");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("1 guerre, 3 points, 3 pièces");
    etapesB.add("1 guerre, 4 points, 4 pièces");

    super.setNom("Le Colosse de Rhodes");
    super.setRessource(RessourceDepart.MINERAI);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
