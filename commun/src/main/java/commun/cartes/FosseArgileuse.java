package commun.cartes;

import commun.Ressource;

public class FosseArgileuse extends CarteRessource{
    public FosseArgileuse() throws Exception {
        super("Fosse Argileuse",new Ressource(), new Ressource("Argile",1), new Ressource("Minerai",1));
        getCout().ajouterRessource("Gold",1);
    }
}
