package commun.plateaux;

import java.util.ArrayList;

public class LePhareDAlexandrie extends Plateau {

  public LePhareDAlexandrie() {
    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("Ressource (matières premières) au choix");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("Ressource (matières premières) au choix");
    etapesB.add("Ressource (produits manufacturés) au choix");
    etapesB.add("7 points");

    super.setNom("Le Phare d'Alexandrie");
    super.setRessource(RessourceDepart.VERRE);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
