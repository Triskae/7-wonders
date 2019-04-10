package commun.cartes;

import commun.Ressource;

public class Caserne extends CarteMilitaire {

    public Caserne() {
        super("caserne", 1,new Ressource());
        try {
            getCout().ajouterRessource("Minerai", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
