package commun.plateaux;

import java.util.ArrayList;

public class LesJardinsSuspendusDeBabylone extends Plateau {

  public LesJardinsSuspendusDeBabylone() {
    ArrayList<String> etapesA = new ArrayList<String>();
    etapesA.add("3 points");
    etapesA.add("Symbole scientifique au choix");
    etapesA.add("7 points");

    ArrayList<String> etapesB = new ArrayList<String>();
    etapesB.add("3 points");
    etapesB.add("Jouer septième carte");
    etapesB.add("Symbole scientifique au choix");

    super.setNom("Les Jardins Suspendus de Babylone");
    super.setRessource(RessourceDepart.ARGILE);
    super.setEtapesA(etapesA);
    super.setEtapesB(etapesB);
    super.setFace('A');
  }

  public String toString() {
    return super.toString();
  }
}
