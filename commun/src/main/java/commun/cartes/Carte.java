package commun.cartes;


import commun.Ressource;
import commun.effets.Effet;

public abstract class Carte {

    private String nom;
    private Ressource cout;

    public Carte(String nom, Ressource cout) {
        this.nom = nom;
        this.cout = cout;
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

    public abstract Effet getEffet();

    public abstract int getType();

    public abstract int getPoint();

    public Ressource getCout() {
        return cout;
    }



}
