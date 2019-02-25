package commun.cartes;

public abstract class CarteMilitaire extends Carte{

    private int nbAttaque;

    public CarteMilitaire(String nom, int nbAttaque) {
        super(nom);
        this.nbAttaque = nbAttaque;
    }

    public int getNbAttaque() {
        return nbAttaque;
    }
}


