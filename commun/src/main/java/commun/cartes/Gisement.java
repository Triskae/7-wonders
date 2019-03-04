package commun.cartes;

import commun.Ressource;

public class Gisement extends CarteRessource {
    public Gisement() throws Exception {
        super("Gisement", new Ressource("Bois",1), new Ressource("Minerai",1));
    }
}
