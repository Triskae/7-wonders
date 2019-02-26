package commun.plateaux;

import java.util.ArrayList;

class LeTempleDArthemisAEphese extends Plateau {

  LeTempleDArthemisAEphese() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("9 ors");
    etapes.add("7 points");

    super.setNom("Le Temple d'Arthemis à Ephese");
    super.setRessource(RessourceDepart.PAPYRUS);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
