package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterRessource;

public class CarteRessource extends Carte{

    public CarteRessource(String nom, Ressource... ressources) {
        super(nom);
        for( Ressource s : ressources ) {
            AjouterRessource aR = new AjouterRessource("AjouterRessource", s);
        }

    }
}
