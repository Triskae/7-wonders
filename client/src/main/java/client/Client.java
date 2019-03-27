package client;

import client.reseau.Connexion;
import commun.Main;
import commun.cartes.Carte;
import client.IA.IA;
import commun.Ressource;
import commun.plateaux.Plateau;
import org.json.JSONArray;

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
    private IA instanceIA;
    private boolean aJoue;

    // Objet de synchro
    private final Object attenteDeconnexion = new Object();

    public Client(String nom, boolean isIA) {
        this.nom = nom;
        this.main = new Main(new ArrayList<>());
        if (isIA) {
            instanceIA = new IA(this, "bleu");
        }
        this.ressources = new Ressource();
    }

    /**
     * un ensemble de getter et setter
     **/

    private int getPointMilitaire() {
        return pointMilitaire;
    }

    public String getNom() {
        return nom;
    }

    private int getNombrePoint() {
        return nombrePoint;
    }

    public Plateau getPlateaux() {
        return plateaux;
    }

    public void setPlateaux(Plateau plateaux) {
        this.plateaux = plateaux;
    }

    private Ressource getRessources() { return ressources; }

    public Main getMain() {
        return this.main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    private void addPoint(int nombrePoint) {
        this.nombrePoint += nombrePoint;
    }

    public void setAJoue(boolean aJoue) {
        this.aJoue = aJoue;
    }

    //    void defausser(Carte carte) {
//        removeCard(carte);
//        nombrePiece += 3;
//    }

    public void setIA(boolean IA) {
        isIA = IA;
    }

    private void removeCard(Carte carte){
        getMain().getCartes().remove(carte);
    }

    private void addPointMilitaire(int point) {
        this.pointMilitaire += point;
    }

    public void tour() {
        if (!aJoue) {
            if (isIA) {
                //appel méthode de jeu classe IA
                instanceIA.tour();
            } else {
                choixUtilisateur();
            }
        } else {
            System.out.println("[CLIENT " + getNom() + "] - Vous avez déjà joué pendant ce tour");
        }
    }

    // Joue une carte au Hasard
    void playCardRand(){
        double rand = (Math.random() * (main.getCartes().size()));
        main.getCartes().remove((int) rand);
    }

    public void playCard(Carte carte) {
        JSONArray payload = new JSONArray();
        payload.put(getNom());
        payload.put(carte.getClass().getName());
        connexion.emit("carteJouee", payload);
        removeCard(carte);
    }

    private void choixUtilisateur() {
        if (!aJoue) {
            Scanner sc = new Scanner(System.in);
            System.out.println("[CLIENT " + getNom() + "] - Vous pouvez jouer une carte");
            System.out.println("[CLIENT " + getNom() + "] - Veuillez saisir le numéro de la carte que vous voulez jouer :");

            for (int i = 0; i < getMain().getCartes().size(); i++) {
                System.out.println(i + " - " + getMain().getCartes().get(i).getNom());
            }

            int nbCartes = Integer.parseInt(sc.nextLine());

            switch (getMain().getCartes().get(nbCartes).getType()){
                case 1 :
                    addPoint(getMain().getCartes().get(nbCartes).getPoint());
                    System.out.println("[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points, vous avez maintenant " + getNombrePoint() + " points");
                    break;
                case 2 :
                    addPointMilitaire(getPointMilitaire() + getMain().getCartes().get(nbCartes).getPoint());
                    System.out.println("[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points militaires, vous avez maintenant " + getPointMilitaire() + " points militaires");
            }
            playCard(getMain().getCartes().get(nbCartes));
            setAJoue(true);
        }
    }

    public void addRessourceDepart(Plateau p) throws Exception {
        switch (p.getNom()) {
            case "La Grande Pyramide de Gizeh":
                this.getRessources().ajouterRessource("Pierre", 1);
                break;
            case "La Statue de Zeus à Olympie":
                this.getRessources().ajouterRessource("Bois", 1);
                break;
            case "Le Colosse de Rhodes":
                this.getRessources().ajouterRessource("Minerai", 1);
                break;
            case "Le Mausolée d'Halicarnasse":
                this.getRessources().ajouterRessource("Tissu", 1);
                break;
            case "Le Phare d'Alexandrie":
                this.getRessources().ajouterRessource("Verre", 1);
                break;
            case "Les Jardins Suspendus de Babylone":
                this.getRessources().ajouterRessource("Argile", 1);
                break;
            case "Le Temple d'Arthemis à Ephese":
                this.getRessources().ajouterRessource("Papyrus", 1);
                break;
        }
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
