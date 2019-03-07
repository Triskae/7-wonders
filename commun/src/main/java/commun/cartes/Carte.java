package commun.cartes;

import java.io.Serializable;

public abstract class Carte implements Serializable {

    private String nom;

    public Carte(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "nom='" + nom + '\'' +
                '}';
    }
}
