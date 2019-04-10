package commun.cartes;

import commun.Ressource;

public class Presse extends CarteRessource {
    public Presse() throws Exception {
        super("Presse",new Ressource(), new Ressource("Papyrus",1));
    }
}
