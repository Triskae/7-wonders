package commun.cartes;

import commun.Ressource;
import commun.Symbole;

public class Atelier extends CarteScientifique {


    public Atelier(){
        super("Atelier",new Ressource(),Symbole.ENGRENAGE);
        try {
            super.getCout().ajouterRessource("Verre", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}