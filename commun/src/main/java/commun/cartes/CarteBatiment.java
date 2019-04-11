package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterPointVictoire;
import commun.effets.Effet;

public abstract class CarteBatiment extends Carte {


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
        return this.getNom() + " [nombre de points = " + nbPoint + "] ";
    }

    public Ressource getCout() {
        return super.getCout();
    }
}
