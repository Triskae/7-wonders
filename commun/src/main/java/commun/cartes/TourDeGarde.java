package commun.cartes;

import commun.Ressource;

public class TourDeGarde extends CarteMilitaire {

    public TourDeGarde() {
        super("tourDeGarde", 1, new Ressource());
        try {
            getCout().ajouterRessource("Argile",1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
