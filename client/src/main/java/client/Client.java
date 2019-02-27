package client;

import client.reseau.Connexion;
import commun.Main;
import commun.plateaux.LaGrandePyramideDeGizeh;
import commun.plateaux.Plateau;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Thread {

    private Connexion connexion;
    private int nombrePiece;
    private int nombrePoint;
    private Plateau plateaux;
    private String nom;
    private Main main;
    private int pointMilitaire;

    // Objet de synchro
    private final Object attenteDeconnexion = new Object();

    private Client(String nom, Main mainJoueur) {
        this.nom = nom;
        this.nombrePiece = 3;
        this.nombrePoint = 0;
        this.plateaux = new LaGrandePyramideDeGizeh();
        this.main = mainJoueur;
    }

    public Client(String nom) {
        this.nom = nom;
        this.main = new Main(new ArrayList<>());
    }



    /**
     * un ensemble de getter et setter
     **/

    public int getPointMilitaire() {
        return pointMilitaire;
    }

    public void setPointMilitaire(int pointMilitaire) {
        this.pointMilitaire = pointMilitaire;
    }

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

    public Main getMain() {
        return this.main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    void addPiece(int nombrePiece){
        this.nombrePiece += nombrePiece;
    }

    void addPoint(int nombrePoint){
        this.nombrePoint += nombrePoint;
    }

    void defausser(String nom){
        removeCard(nom);
        nombrePiece += 3;
    }

    private void removeCard(String nom){
        int i = 0;
        for(int compt = 0; compt < main.getCartes().size(); compt++){
            if(nom.equals(main.getCartes().get(i).getNom())){
                main.getCartes().remove(i);
            }
            i++;
        }
    }

    public void addPointMilitaire(int point){
        this.pointMilitaire+= point;
    }

    // Joue une carte au Hasard
    void playCard(){
        double rand = (Math.random() * (main.getCartes().size()));
        main.getCartes().remove((int) rand);
    }

    public String toString() {
        return "Nom : /n" + this.nom +
                "Nombre de pièces : /n" + this.nombrePiece +
                "Nombre de points : /n" + this.nombrePoint;
    }

    private void seConnecter() {
        // on se connecte
        this.connexion.seConnecter();
        synchronized (attenteDeconnexion) {
            try {
                attenteDeconnexion.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onConnexion() {
        System.out.println("[CLIENT " + getNom() + "] - Connexion réussie");
    }

    public void finPartie() {
        synchronized (attenteDeconnexion) {
            attenteDeconnexion.notify();
        }
    }

    @Override
    public void run() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("[CLIENT " + getNom() + "] - Initialisation");

        int port = 60001;

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ipAdress = null;
        if (inetAddress != null) {
            ipAdress = inetAddress.getHostAddress();
        }

        Client c = new Client("client", this.main);
        connexion = new Connexion("http://"+ ipAdress + ":" + port, c);
        c.seConnecter();

        System.out.println("[CLIENT " + getNom() + "] - Déconnexion");
    }
}
