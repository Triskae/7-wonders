package commun.cartes;


public abstract class Carte {

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

    public abstract int getType();

    public abstract int getPoint();

}
