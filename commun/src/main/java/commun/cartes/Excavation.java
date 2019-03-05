package commun.cartes;

import commun.Ressource;

public class Excavation extends CarteRessource {
    public Excavation() throws Exception {
        super("Excavation", new Ressource("Pierre",1), new Ressource("Argile",1));
    }
}
