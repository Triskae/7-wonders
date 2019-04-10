package commun.cartes;

import commun.Ressource;
import commun.Symbole;

public class Scriptorium extends CarteScientifique {


    public Scriptorium(){
        super("Scriptorium",new Ressource(),Symbole.TABLETTE);
        try {
            getCout().ajouterRessource("Papyrus", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}