package commun;

import java.util.HashMap;
import java.util.Map;

public class Ressource {

    private HashMap<String, Integer>ressource = new HashMap<>();

    public Ressource(){
        initRessource();
    }

    public Ressource(String nom, int nb) throws Exception {
        initRessource();
        this.ajouterRessource(nom, nb);
    }

    private void initRessource(){
        ressource.put("Bois",0);
        ressource.put("Pierre",0);
        ressource.put("Minerai",0);
        ressource.put("Argile",0);
        ressource.put("Tissu",0);
        ressource.put("Verre",0);
        ressource.put("Papyrus",0);
        ressource.put("Gold",0);
    }

    public void ajouterRessource(String nom, int nb) {
        if(nb <= 0){
            try {
                throw new Exception("Valeur negative interdite");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ressource.containsKey(nom)) {
            ressource.put(nom,ressource.get(nom)+ nb);
        }else{
            try {
                throw new Exception("Ressource inconnue");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public HashMap<String, Integer> getRessources() {
        return ressource;
    }

    public HashMap<String, Integer> getRessourcesSansValeursZero() {
        HashMap<String, Integer> hashMapRetour = new HashMap<>();
        for (Map.Entry<String, Integer> entrees : ressource.entrySet()) {
            if (entrees.getValue() != 0) hashMapRetour.put(entrees.getKey(), entrees.getValue());
        }
        return hashMapRetour;
    }

    public Integer getRessource(String type) {
        return ressource.get(type);
    }

    public void setRessource(String type, int valeur) {
        ressource.put(type, valeur);
    }

    @Override
    public String toString() {
        return "Ressource{" +
                "ressource=" + ressource +
                '}';
    }
}
