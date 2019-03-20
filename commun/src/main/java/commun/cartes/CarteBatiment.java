package commun.cartes;

import commun.effets.AjouterPointVictoire;

public abstract class CarteBatiment extends Carte {

    private int nbPoint;

    public CarteBatiment(String nom, int nbPoint) {
        super(nom);
        this.nbPoint = nbPoint;
        AjouterPointVictoire ap = new AjouterPointVictoire("AjouterPointVictoire", nbPoint);
    }

    public int getPoint() {
        return nbPoint;
    }

    public int getType(){
        return 1;
    }

    @Override
    public String toString() {
        return this.getNom() + " [nombre de points = " + nbPoint + "] ";
    }
}
