package commun.plateaux;

import java.util.ArrayList;

public class LeTempleDArthemisAEphese extends Plateau {

  public LeTempleDArthemisAEphese() {

    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("9 pièces");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("2 points, 4 pièces");
    etapesB.add("3 points, 4 pièces");
    etapesB.add("5 points, 4 pièces");


    super.setNom("Le Temple d'Arthemis à Ephese");
    super.setRessource(RessourceDepart.PAPYRUS);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
