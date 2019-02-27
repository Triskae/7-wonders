package commun.plateaux;

import java.util.ArrayList;

public class LaStatueDeZeusAOlympie extends Plateau {

  public LaStatueDeZeusAOlympie() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Batiment gratuit");
    etapes.add("7 points");

    super.setNom("La Statue de Zeus à Olympie");
    super.setRessource(RessourceDepart.BOIS);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
