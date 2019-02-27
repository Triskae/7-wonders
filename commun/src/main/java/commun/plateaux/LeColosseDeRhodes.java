package commun.plateaux;

import java.util.ArrayList;

public class LeColosseDeRhodes extends Plateau {

  public LeColosseDeRhodes() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("2 guerres");
    etapes.add("7 points");

    super.setNom("Le Colosse de Rhodes");
    super.setRessource(RessourceDepart.MINERAI);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
