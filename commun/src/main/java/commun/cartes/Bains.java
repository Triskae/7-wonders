package commun.cartes;

import commun.Ressource;

public class Bains extends CarteBatiment {

    public Bains() {
        super("bains", 3,new Ressource());
        try {
            getCout().ajouterRessource("Pierre",1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
