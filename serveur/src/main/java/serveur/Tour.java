package serveur;


import commun.cartes.Carte;

public class Tour {
    private int typeInteraction;
    private Carte carteJouee;
    private String nomJoueur;
    private int indice;


    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getTypeInteraction() {
        return typeInteraction;
    }

    public void setTypeInteraction(int typeInteraction) {
        this.typeInteraction = typeInteraction;
    }

    public Carte getCarteJouee() {
        return carteJouee;
    }

    public void setCarteJouee(Carte carteJouee) {
        this.carteJouee = carteJouee;
    }

    public String getNomJoueur() {
        return nomJoueur;
    }

    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    public Tour(int typeInteraction, Carte carteJouee, String nomJoueur, int indiceCarte) {
        this.typeInteraction = typeInteraction;
        this.carteJouee = carteJouee;
        this.nomJoueur = nomJoueur;
        this.indice = indiceCarte;
    }
}
