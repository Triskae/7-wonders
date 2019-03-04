package commun;

import java.util.HashMap;

public class Ressource {

    private HashMap<String, Integer>ressource = new HashMap<>();;

    public Ressource(){
        initRessource();
    }

    public Ressource(String nom, int nb) throws Exception {
        initRessource();
        this.ajouterRessource(nom, nb);
    }

    public void initRessource(){
        ressource.put("Bois",0);
        ressource.put("Pierre",0);
        ressource.put("Minerai",0);
        ressource.put("Argile",0);
        ressource.put("Tissu",0);
        ressource.put("Verre",0);
        ressource.put("Papyrus",0);
    }

    public void ajouterRessource(String nom, int nb) throws Exception {
        if(nb <= 0){
            throw new Exception("Valeur negative interdite");
        }
        if (ressource.containsKey(nom)) {
            ressource.put(nom,ressource.get(nom)+ nb);
        }else{
            throw new Exception("Ressource inconnue");
        }

    }

    public Integer getRessource(String type) {
        return ressource.get(type);
    }


}
