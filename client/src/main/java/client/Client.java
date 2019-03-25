package client;

import client.reseau.Connexion;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import commun.Main;
import commun.cartes.Carte;
import client.IA.IA;
import commun.Ressource;
import commun.plateaux.Plateau;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Thread {

    private Connexion connexion;
    private int nombrePiece;
    private int nombrePoint;
    private Plateau plateaux;
    private Ressource ressources;
    private String nom;
    private Main main;
    private int pointMilitaire;
    private boolean isIA=false;
    private Terminal terminal;
    private Screen screen;
    private IA instanceIA;

    // Objet de synchro
    private final Object attenteDeconnexion = new Object();

    private Client(String nom, Main mainJoueur) {
        this.nom = nom;
        this.nombrePiece = 3;
        this.nombrePoint = 0;
        this.main = mainJoueur;
    }

    public Client(String nom, boolean isIA) {
        this.nom = nom;
        this.main = new Main(new ArrayList<>());
        if (isIA) {
            instanceIA = new IA(this, "");
        }
        this.ressources = new Ressource();
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

    public Ressource getRessources() { return ressources; }

    public Main getMain() {
        return this.main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    void addPiece(int nombrePiece) {
        this.nombrePiece += nombrePiece;
    }

    void addPoint(int nombrePoint) {
        this.nombrePoint += nombrePoint;
    }

    void defausser(Carte carte) {
        removeCard(carte);
        nombrePiece += 3;
    }

    public boolean isIA() {
        return isIA;
    }

    public void setIA(boolean IA) {
        isIA = IA;
    }

    private void removeCard(Carte carte){
        getMain().getCartes().remove(carte);
    }

    public void addPointMilitaire(int point) {
        this.pointMilitaire += point;
    }

    public void tour() {
        if (isIA) {
            //appel méthode de jeu classe IA
            instanceIA.tour();
        } else {
            choixUtilisateur();
        }
    }
    // Joue une carte au Hasard
    void playCardrand(){
        double rand = (Math.random() * (main.getCartes().size()));
        main.getCartes().remove((int) rand);
    }

    public void playCard(Carte carte){
        connexion.emit("carteJouee", carte.getClass().getName());
        removeCard(carte);
    }

    public void choixUtilisateur() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez saisir le numéro de la carte que vous voulez jouer :");

        for (int i = 0; i < getMain().getCartes().size(); i++) {
            System.out.println(i + " - " + getMain().getCartes().get(i).getNom());
        }

        int nbCartes = Integer.parseInt(sc.nextLine());


        switch (getMain().getCartes().get(nbCartes).getType()){
            case 1 :
                addPoint(getMain().getCartes().get(nbCartes).getPoint());
                break;
            case 2 :
                setPointMilitaire(getPointMilitaire() + getMain().getCartes().get(nbCartes).getPoint());
        }
        playCard(getMain().getCartes().get(nbCartes));
    }

    public void addRessourceDepart(Plateau p) throws Exception {
        System.out.println(this.getNom());

        if(p.getNom().equals("La Grande Pyramide de Gizeh"))
            this.getRessources().ajouterRessource("Pierre", 1);

        else if(p.getNom().equals("La Statue de Zeus à Olympie"))
            this.getRessources().ajouterRessource("Bois", 1);

        else if(p.getNom().equals("Le Colosse de Rhodes"))
            this.getRessources().ajouterRessource("Minerai", 1);

        else if(p.getNom().equals("Le Mausolée d'Halicarnasse"))
            this.getRessources().ajouterRessource("Tissu", 1);

        else if(p.getNom().equals("Le Phare d'Alexandrie"))
            this.getRessources().ajouterRessource("Verre", 1);

        else if(p.getNom().equals("Les Jardins Suspendus de Babylone"))
            this.getRessources().ajouterRessource("Argile", 1);

        else if(p.getNom().equals("Le Temple d'Arthemis à Ephese"))
            this.getRessources().ajouterRessource("Papyrus", 1);
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
        connexion.emit("envoiIdentification", getNom());
    }

    public void finPartie() {
        synchronized (attenteDeconnexion) {
            attenteDeconnexion.notify();
        }
    }

    public void readyToPlay() {
        // Ici peux jouer
        System.out.println("[CLIENT " + getNom() + "] - Main reçue");
        System.out.println("[CLIENT " + getNom() + "] - " + getMain());
        connexion.emit("playerReady");
    }

    /*public void jouerMain() {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            ActionListDialogBuilder actionListDialogBuilder = new ActionListDialogBuilder();
            actionListDialogBuilder.setTitle("Choisi ta carte");


            for (int i = 0; i < main.getCartes().size(); i++) {
                int finalI = i;
                actionListDialogBuilder.addAction(main.getCartes().get(i).getNom(), () -> {
//                    connexion.emit("carteJouee", getMain().getCartes().get(finalI).getClass().getName());

                });
            }
            actionListDialogBuilder.build().showDialog(textGUI);
            screen.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void run() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("[CLIENT " + getNom() + "] - Initialisation");

        int port = 60001;

        connexion = new Connexion("http://" + "127.0.0.1" + ":" + port, this);
        this.seConnecter();
    }
}
