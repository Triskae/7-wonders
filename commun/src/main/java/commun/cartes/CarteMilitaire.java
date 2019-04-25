package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterBoucliers;
import commun.effets.Effet;

public abstract class CarteMilitaire extends Carte{

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BOLD = "\033[0;1m";

    private AjouterBoucliers am;
    private int nbAttaque;

    CarteMilitaire(String nom, int nbAttaque, Ressource cout) {
        super(nom,cout);
        this.nbAttaque = nbAttaque;
        am = new AjouterBoucliers("AjouterBoucliers", nbAttaque);
    }

    public int getPoint() {
        return nbAttaque;
    }

    public int getType(){
        return 2;
    }

    @Override
    public Effet getEffet() {
        return am;
    }

    @Override
    public String toString() {
        return (ANSI_BOLD + ANSI_GREEN + this.getNom() + ANSI_RESET + ANSI_GREEN + " : coût -> " + ANSI_BOLD + ANSI_GREEN + this.getCout().getRessourcesSansValeursZero() + ANSI_RESET + ANSI_GREEN + " / effet -> " + ANSI_BOLD + ANSI_GREEN + "Ajoute " + nbAttaque + " boucliers" + ANSI_RESET).replace("{", "").replace("}", "");
    }

    public Ressource getCout() {
        return super.getCout();
    }
}


