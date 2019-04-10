package commun.cartes;

import commun.Ressource;

public class Mine extends CarteRessource {
    public Mine() throws Exception {
        super("Mine",new Ressource(), new Ressource("Pierre",1), new Ressource("Minerai",1));
        getCout().ajouterRessource("Gold",1);
    }
}
