package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterPointVictoire;
import commun.effets.Effet;

public abstract class CarteBatiment extends Carte {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BOLD = "\033[0;1m";

    private AjouterPointVictoire ap;
    private int nbPoint;

    public CarteBatiment(String nom, int nbPoint, Ressource cout) {
        super(nom,cout);
        this.nbPoint = nbPoint;
        ap = new AjouterPointVictoire("AjouterPointVictoire", nbPoint);
    }

    public int getPoint() {
        return nbPoint;
    }

    public int getType(){
        return 1;
    }

    @Override
    public Effet getEffet() {
        return ap;
    }

    @Override
    public String toString() {
        if (this.getCout().getRessourcesSansValeursZero().isEmpty()) {
            return (ANSI_BOLD + ANSI_GREEN + this.getNom() + ANSI_RESET + ANSI_GREEN + " : coût -> " + ANSI_BOLD + ANSI_GREEN + "gratuit" + ANSI_RESET + ANSI_GREEN + " / effet -> " + ANSI_BOLD + ANSI_GREEN + "Ajoute " + nbPoint + " points de victoire" + ANSI_RESET).replace("{", "").replace("}", "");
        } else {
            return (ANSI_BOLD + ANSI_GREEN + this.getNom() + ANSI_RESET + ANSI_GREEN + " : coût -> " + ANSI_BOLD + ANSI_GREEN + this.getCout().getRessourcesSansValeursZero() + ANSI_RESET + ANSI_GREEN + " / effet -> " + ANSI_BOLD + ANSI_GREEN + "Ajoute " + nbPoint + " points de victoire" + ANSI_RESET).replace("{", "").replace("}", "");
        }
    }

    public Ressource getCout() {
        return super.getCout();
    }
}
