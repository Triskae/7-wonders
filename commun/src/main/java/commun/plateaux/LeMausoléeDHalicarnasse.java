package commun.plateaux;

import java.util.ArrayList;

public class LeMausoléeDHalicarnasse extends Plateau {

  public LeMausoléeDHalicarnasse() {
    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("Défaussées gratuit");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("2 points, Défaussées gratuit");
    etapesB.add("1 point, Défaussées gratuit");
    etapesB.add("Défaussées gratuit");

    super.setNom("Le Mausolée d'Halicarnasse");
    super.setRessource(RessourceDepart.TISSU);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
