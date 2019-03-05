package commun.cartes;

import commun.effets.AjouterPointVictoire;

public abstract class CarteBatiment extends Carte {

    private int nbPoint;

    public CarteBatiment(String nom, int nbPoint) {
        super(nom);
        this.nbPoint = nbPoint;
        AjouterPointVictoire ap = new AjouterPointVictoire("AjouterPointVictoire", nbPoint);
    }

    public int getNbPoint() {
        return nbPoint;
    }

    @Override
    public String toString() {
        return this.getNom() + " [nombre de points = " + nbPoint + "] ";
    }
}
