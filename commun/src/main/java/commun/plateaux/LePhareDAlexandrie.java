package commun.plateaux;

import java.util.ArrayList;

class LePhareDAlexandrie extends Plateau {

  LePhareDAlexandrie() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Ressource (matières premières) au choix");
    etapes.add("7 points");

    super.setRessource(RessourceDepart.VERRE);
    super.setEtapes(etapes);
  }

  public String toString() {
   return "LePhareDAlexandrie : < " + super.toString() + " >";
  }
}
