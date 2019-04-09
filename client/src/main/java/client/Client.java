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

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

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
            setIA(true);
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

    public boolean getAJoue() {
        return aJoue;
    }

    public void setAJoue(boolean aJoue) {
        this.aJoue = aJoue;
    }

    public void setIA(boolean IA) {
        isIA = IA;
    }

    public boolean isIA() {
        return isIA;
    }

    private void removeCard(Carte carte){
        getMain().getCartes().remove(carte);
    }

    private void addPointMilitaire(int point) {
        this.pointMilitaire += point;
    }

    public void tour() {
        if (!aJoue) {
            if (isIA()) instanceIA.tour();
            else choixUtilisateur();
        } else {
            if (!isIA()) System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Vous avez déjà joué pendant ce tour" + ANSI_RESET);
        }
    }

    public void playCard(Carte carte, int indiceCarte) {
        JSONArray payload = new JSONArray();
        payload.put(getNom());
        payload.put(carte.getClass().getName());
        payload.put(indiceCarte);
        connexion.emit("carteJouee", payload);
        removeCard(carte);
    }

    private void choixUtilisateur() {
        if (!aJoue && !isIA()) {
            Scanner sc = new Scanner(System.in);
            int nbCartes = -1;
            System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Vous pouvez jouer une carte" + ANSI_RESET);
            for (int i = 0; i < getMain().getCartes().size(); i++) {
                System.out.println(ANSI_YELLOW + i + " - " + getMain().getCartes().get(i).getNom() + ANSI_YELLOW);
            }

            while (nbCartes < 0 || nbCartes > getMain().getCartes().size() - 1) {
                System.out.println(ANSI_CYAN + "[CLIENT " + getNom() + "] - Veuillez saisir le numéro de la carte que vous voulez jouer :" + ANSI_RESET);
                nbCartes = Integer.parseInt(sc.nextLine());
            }

            switch (getMain().getCartes().get(nbCartes).getType()){
                case 1 :
                    addPoint(getMain().getCartes().get(nbCartes).getPoint());
                    if (isIA()) System.out.println(ANSI_PURPLE + "[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points, vous avez maintenant " + getNombrePoint() + " points" + ANSI_RESET);
                    else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points, vous avez maintenant " + getNombrePoint() + " points" + ANSI_RESET);
                    break;
                case 2 :
                    addPointMilitaire(getPointMilitaire() + getMain().getCartes().get(nbCartes).getPoint());
                    if (isIA()) System.out.println(ANSI_PURPLE +"[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points militaires, vous avez maintenant " + getPointMilitaire() + " points militaires" + ANSI_RESET);
                    else System.out.println(ANSI_YELLOW +"[CLIENT " + getNom() + "] - Vous avez joué " + getMain().getCartes().get(nbCartes) + " et avez ainsi gagné " + getMain().getCartes().get(nbCartes).getPoint() + " points militaires, vous avez maintenant " + getPointMilitaire() + " points militaires" + ANSI_RESET);
            }
            playCard(getMain().getCartes().get(nbCartes), getMain().getCartes().indexOf(getMain().getCartes().get(nbCartes)));
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
        if (isIA()) System.out.println(ANSI_PURPLE + "[CLIENT " + getNom() + "] - Connexion réussie" + ANSI_RESET);
        else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Connexion réussie" + ANSI_RESET);
        connexion.emit("envoiIdentification", getNom());
    }

    public void finPartie() {
        synchronized (attenteDeconnexion) {
            attenteDeconnexion.notify();
        }
    }

    public void readyToPlay() {
        if (!isIA()) System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Nouvelle main reçue" + ANSI_RESET);
        connexion.emit("playerReady");
    }

    @Override
    public void run() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (isIA()) System.out.println(ANSI_PURPLE + "[CLIENT " + getNom() + "] - Initialisation" + ANSI_RESET);
        else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Initialisation" + ANSI_RESET);

        int port = 60001;

        connexion = new Connexion("http://" + "127.0.0.1" + ":" + port, this);
        this.seConnecter();
    }
}
