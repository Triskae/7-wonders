package commun.joueur;

import commun.cartes.Carte;
import commun.cartes.CarteBatiment;
import commun.plateaux.LaGrandePyramideDeGizeh;
import commun.plateaux.Plateau;

import java.util.ArrayList;
import java.util.Random;

public class Joueur {
    private String nom;
    private int nombrePiece;
    private int nombrePoint;
    private Plateau plateaux;
    private ArrayList<CarteBatiment> main;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNombrePiece() {
        return nombrePiece;
    }

    public void setNombrePiece(int nombrePiece) {
        this.nombrePiece = nombrePiece;
    }

    public int getNombrePoint() {
        return nombrePoint;
    }

    public void setNombrePoint(int nombrePoint) {
        this.nombrePoint = nombrePoint;
    }

    public Plateau getPlateaux() {
        return plateaux;
    }

    public void setPlateaux(Plateau plateaux) {
        this.plateaux = plateaux;
    }

    public ArrayList<CarteBatiment> getMain() {
        return main;
    }

    public void setMain(ArrayList<CarteBatiment> deck) {
        this.main = deck;
    }

    public Joueur(String nom){
        this.nom = nom;
        this.nombrePiece=3;
        this.nombrePoint=0;
        this.plateaux= new LaGrandePyramideDeGizeh();
        ArrayList<Carte> deck = new ArrayList();

    }

    void addpiece(int nombrePiece){
        this.nombrePiece+= nombrePiece;
    }

    void addPoint(int nombrePoint){
        this.nombrePoint+= nombrePoint;
    }

    void defausser(String nom){
        removeCard(nom);
        nombrePiece+=3;
    }

    void removeCard(String nom){
        int i = 0;
        for(int compt = 0; compt<main.size();compt++){
            if(nom.equals(main.get(i).getNom())){
                main.remove(i);
            }
            i++;
        }
    }

    //Joue une carte au Hasard
    void playCard(){
       double rand = (Math.random()*(main.size()));
       main.get((int)rand).getNbPoint();
       main.remove(rand);

    }
    public String toString() {
        return "Nom : /n" + this.nom +
                "Nombre de pièces : /n" + this.nombrePiece +
                "Nombre de points : /n" + this.nombrePoint;
    }
}

