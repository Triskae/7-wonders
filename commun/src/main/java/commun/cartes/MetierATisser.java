package commun.cartes;

import commun.Ressource;

public class MetierATisser extends CarteRessource {
    public MetierATisser() throws Exception {
        super("Metier A Tisser",new Ressource(), new Ressource("Tissu",1));
    }
}
