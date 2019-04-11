package commun.effets;

import commun.Ressource;

import java.util.HashMap;
import java.util.Map;

public class AjouterRessource extends Effet {

    private HashMap<String, Integer> ressources = new HashMap<>();

    public AjouterRessource(String nom, Ressource s) {
        super(nom);
        for (Map.Entry<String, Integer> entry : s.getRessources().entrySet()) {
            ressources.put(entry.getKey(), entry.getValue());
        }
    }

    public HashMap<String, Integer> getRessources() {
        return ressources;
    }

    public HashMap<String, Integer> getRessourcesSansValeursZero() {
        HashMap<String, Integer> hashMapRetour = new HashMap<>();
        for (Map.Entry<String, Integer> entrees : ressources.entrySet()) {
            if (entrees.getValue() != 0) hashMapRetour.put(entrees.getKey(), entrees.getValue());
        }
        return hashMapRetour;
    }

    public int getNbDePoint(){return 0;}
}
