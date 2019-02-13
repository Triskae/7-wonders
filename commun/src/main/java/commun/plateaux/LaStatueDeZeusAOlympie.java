package commun.plateaux;

import java.util.ArrayList;

class LaStatueDeZeusAOlympie extends Plateau {

  LaStatueDeZeusAOlympie() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Batiment gratuit");
    etapes.add("7 points");

    super.setRessource(RessourceDepart.BOIS);
    super.setEtapes(etapes);
  }

  public String toString() {
   return "LaStatueDeZeusAOlympie : < " + super.toString() + " >";
  }
}
