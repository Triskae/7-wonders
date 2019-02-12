package commun.cartes;

import commun.effets.AjouterPointVictoire;

public abstract class CarteBatiment extends Carte {

    private int nbPoint;

    public CarteBatiment(String nom, int nbPoint, int id){
        super(nom,id);
        this.nbPoint = nbPoint;
        AjouterPointVictoire ap = new AjouterPointVictoire("AjouterPointVictoire",nbPoint);
    }

}
