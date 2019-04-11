package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterRessource;

public class CarteRessource extends Carte{

    private static final String ANSI_BOLD = "\033[0;1m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private AjouterRessource aR;

    public CarteRessource(String nom, Ressource cout, Ressource... ressources) {
        super(nom, cout);
        for (Ressource s : ressources) {
            aR = new AjouterRessource("AjouterRessource", s);
        }
    }

    @Override
    public String toString() {
        if (this.getCout().getRessourcesSansValeursZero().isEmpty()) {
            return (ANSI_BOLD + ANSI_GREEN + this.getNom() + ANSI_RESET + ANSI_GREEN + " : coût -> " + ANSI_BOLD + ANSI_GREEN + "gratuite" + ANSI_RESET + ANSI_GREEN + " / effet -> " + ANSI_BOLD + ANSI_GREEN + "Ajoute " + aR.getRessourcesSansValeursZero() + ANSI_RESET).replace("{", "").replace("}", "");
        } else {
            return (ANSI_BOLD + ANSI_GREEN + this.getNom() + ANSI_RESET + ANSI_GREEN + " : coût -> " + ANSI_BOLD + ANSI_GREEN + this.getCout().getRessourcesSansValeursZero() + ANSI_RESET + ANSI_GREEN + " / effet -> " + ANSI_BOLD + ANSI_GREEN + "Ajoute " + aR.getRessourcesSansValeursZero() + ANSI_RESET).replace("{", "").replace("{", "").replace("}", "");
        }
    }

    public int getType(){
        return 3;
    }

    @Override
    public AjouterRessource getEffet() {
        return aR;
    }

    public int getPoint(){return 0;}


}
