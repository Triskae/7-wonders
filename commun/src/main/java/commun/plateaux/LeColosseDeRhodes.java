package commun.plateaux;

import java.util.ArrayList;

class LeColosseDeRhodes extends Plateau {

  LeColosseDeRhodes() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("2 guerres");
    etapes.add("7 points");

    super.setRessource(RessourceDepart.MINERAI);
    super.setEtapes(etapes);
  }

  public String toString() {
   return "LeColosseDeRhodes : < " + super.toString() + " >";
  }
}