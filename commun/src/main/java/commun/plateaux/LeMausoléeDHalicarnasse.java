package commun.plateaux;

import java.util.ArrayList;

class LeMausoléeDHalicarnasse extends Plateau {

  LeMausoléeDHalicarnasse() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Défaussées gratuit");
    etapes.add("7 points");

    super.setRessource(RessourceDepart.TISSU);
    super.setEtapes(etapes);
  }

  public String toString() {
   return "LeMausoléeDHalicarnasse : < " + super.toString() + " >";
  }
}
