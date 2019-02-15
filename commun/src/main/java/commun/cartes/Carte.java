package commun.cartes;

public abstract class Carte {

    private String nom;

    public Carte(String nom, int id) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}
