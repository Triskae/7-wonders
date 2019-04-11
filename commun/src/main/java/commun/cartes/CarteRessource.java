package commun.cartes;

import commun.Ressource;
import commun.effets.AjouterRessource;

public class CarteRessource extends Carte{


    private AjouterRessource aR;

    public CarteRessource(String nom, Ressource cout, Ressource... ressources) {
        super(nom, cout);
        for (Ressource s : ressources) {
            aR = new AjouterRessource("AjouterRessource", s);
        }
    }

    @Override
    public String toString() {
        return this.getNom();
    }

    public int getType(){
        return 3;
    }

    @Override
    public AjouterRessource getEffet() {
        return aR;
    }

    public int getPoint(){return 0;}


}
