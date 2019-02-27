package commun.plateaux;

import java.util.ArrayList;

public class LesJardinsSuspendusDeBabylone extends Plateau {

  public LesJardinsSuspendusDeBabylone() {
    ArrayList<String> etapes = new ArrayList<String>();
    etapes.add("3 points");
    etapes.add("Symbole scinetifique au choix");
    etapes.add("7 points");

    super.setNom("Les Jardins Suspendus de Babylone");
    super.setRessource(RessourceDepart.PIERRE);
    super.setEtapes(etapes);
  }

  public String toString() {
    return super.toString();
  }
}
