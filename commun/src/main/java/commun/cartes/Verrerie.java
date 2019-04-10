package commun.cartes;

import commun.Ressource;

public class Verrerie extends CarteRessource {
    public Verrerie() throws Exception {
        super("Verrerie",new Ressource(), new Ressource("Verre",1));
    }
}
