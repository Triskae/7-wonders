package commun.cartes;

import commun.Ressource;

public class Palissade extends CarteMilitaire{

    public Palissade() {
        super("palissade", 1, new Ressource());
        try {
            getCout().ajouterRessource("Bois",1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
