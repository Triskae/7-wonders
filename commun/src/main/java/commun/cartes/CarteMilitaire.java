package commun.cartes;

import commun.effets.AjouterPointMilitaire;

public abstract class CarteMilitaire extends Carte{

    private int nbAttaque;

    public CarteMilitaire(String nom, int nbAttaque) {
        super(nom);
        this.nbAttaque = nbAttaque;
        AjouterPointMilitaire am = new AjouterPointMilitaire("AjouterPointMilitaire", nbAttaque);
    }

    public int getPoint() {
        return nbAttaque;
    }

    public int getType(){
        return 2;
    }

    @Override
    public String toString() {
        return this.getNom() + " [nombre de points d'attaque = " + nbAttaque + "] ";
    }
}


