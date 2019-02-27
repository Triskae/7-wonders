package commun.plateaux;

import java.util.ArrayList;

public class LePhareDAlexandrie extends Plateau {

  public LePhareDAlexandrie() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Ressource (matières premières) au choix");
    etapes.add("7 points");

    super.setNom("Le Phare d'Alexandrie");
    super.setRessource(RessourceDepart.VERRE);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
