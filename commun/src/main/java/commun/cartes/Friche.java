package commun.cartes;

import commun.Ressource;

public class Friche extends CarteRessource{

    public Friche() throws Exception {
        super("Friche",new Ressource(), new Ressource("Bois",1), new Ressource("Argile",1));
        getCout().ajouterRessource("Gold",1);
    }
}
