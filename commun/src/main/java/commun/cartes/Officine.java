package commun.cartes;

import commun.Ressource;
import commun.Symbole;

public class Officine extends CarteScientifique {


    public Officine(){
        super("Officine",new Ressource(),Symbole.COMPAS);
        try {
            getCout().ajouterRessource("Tissu", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}