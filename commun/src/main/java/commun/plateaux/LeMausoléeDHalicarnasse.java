package commun.plateaux;

import java.util.ArrayList;

public class LeMausoléeDHalicarnasse extends Plateau {

  public LeMausoléeDHalicarnasse() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Défaussées gratuit");
    etapes.add("7 points");

    super.setNom("Le Mausolée d'Halicarnasse");
    super.setRessource(RessourceDepart.TISSU);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
