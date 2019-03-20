package commun.plateaux;

import java.util.ArrayList;

public class LaStatueDeZeusAOlympie extends Plateau {

  public LaStatueDeZeusAOlympie() {

    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("Batiment gratuit");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("Comptoirs");
    etapesB.add("5 points");
    etapesB.add("Choix guilde");

    super.setNom("La Statue de Zeus à Olympie");
    super.setRessource(RessourceDepart.BOIS);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
