package client;

import client.IA.IA;
import client.reseau.Connexion;
import commun.Main;
import commun.Ressource;
import commun.cartes.Carte;
import commun.effets.AjouterRessource;
import commun.plateaux.Plateau;
import org.json.JSONArray;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Client extends Thread {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private Connexion connexion;
    private int nombrePointBatiments;
    private Plateau plateaux;
    private Ressource ressources;
    private String nom;
    private Main main;
    private boolean isIA = false;
    private IA instanceIA;
    private boolean aJoue;
    private int boucliers;
    private int pointsVictoire = 0;
    private final Object attenteDeconnexion = new Object(); // Objet de synchro

    public Client(String nom, boolean isIA, String strat) {
        this.nom = nom;
        this.main = new Main(new ArrayList<>());
        this.boucliers = 0;
        if (isIA) {
            instanceIA = new IA(this, strat);
            setIA(true);
        }
        this.ressources = new Ressource();
        try {
            this.ressources.ajouterRessource("Gold",3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * un ensemble de getter et setter
     **/
    public int getNbBoucliers() {
        return boucliers;
    }

    public String getNom() {
        return nom;
    }

    private int getNombrePointBatiment() {
        return nombrePointBatiments;
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

    private void addPointBatiments(int nombrePoint) {
        this.nombrePointBatiments += nombrePoint;
    }

    public boolean getAJoue() {
        return aJoue;
    }

    public int getNombrePiece() {
        return ressources.getRessource("Gold");
    }

    public void setNombrePiece(int nombrePiece) {
        ressources.setRessource("Gold", nombrePiece);
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

    public IA getInstanceIA() {
        return instanceIA;
    }

    private void removeCard(Carte carte){
        getMain().getCartes().remove(carte);
    }

    private void addBoucliers(int point) {
        this.boucliers += point;
    }

    public void tour(boolean isNouveauTour){
        if (!aJoue) {
            if (isIA()) instanceIA.tour();
            else choixUtilisateur(isNouveauTour);
        } else {
            if (!isIA()) System.out.println(ANSI_RED + "[CLIENT " + getNom() + "] - Vous avez déjà joué pendant ce tour" + ANSI_RESET);
        }
    }

    /**
     * Permet d'effectuer l'action de défausse d'une carte
     * @param carte l'instance de la carte à défausser
     * @param indiceCarte l'indice de la position de la carte dans la main du joueur, utilisé par le serveur
     */
    private void defausserCarte(Carte carte, int indiceCarte) {
        JSONArray payload = new JSONArray();
        payload.put(getNom());
        payload.put(carte.getClass().getName());
        payload.put(indiceCarte);
        payload.put(1);
        connexion.emit("jeu", payload);
        removeCard(carte);
    }

    /**
     * Permet de jouer une carte
     * @param carte l'instance de la carte à jouer
     * @param indiceCarte l'indice de la position de la carte dans la main du joueur, utilisé par le serveur
     */
    public void playCard(Carte carte, int indiceCarte) {
        int nbVerifications = nombreDeVerifications(carte);
        int nbValidations = 0;
        boolean jeuPossible = false;

        if (nbVerifications == 0) {
            jeuPossible = true;
        } else {
            for (Map.Entry<String, Integer> coutsCarte : carte.getCout().getRessources().entrySet()) {
                if (coutsCarte.getValue() > 0 && ressources.getRessources().get(coutsCarte.getKey()) >= coutsCarte.getValue()) {
                    nbValidations++;
                }
            }
        }

        if (nbValidations == nbVerifications) jeuPossible = true;

        if (jeuPossible) {
            System.out.println("jeu possible par " + getNom() + " joue la carte " + carte);
            JSONArray payload = new JSONArray();
            payload.put(getNom());
            payload.put(carte.getClass().getName());
            payload.put(indiceCarte);
            payload.put(2);

            connexion.emit("jeu", payload);
            plateaux.ajouterCarteJouee(carte);
            if (carte.getEffet() instanceof AjouterRessource) {
                for (Map.Entry<String, Integer> ressourcesCarte : ((AjouterRessource) carte.getEffet()).getRessources().entrySet()) {
                    if (ressourcesCarte.getValue() != 0) ressources.ajouterRessource(ressourcesCarte.getKey(), ressourcesCarte.getValue());
                }
            }
            //TODO Retirer si besoin le remove card
            //removeCard(carte);
            switch (carte.getType()){
                case 1 :
                    addPointBatiments(carte.getPoint());
                    if (!isIA()) System.out.println(ANSI_GREEN + "[CLIENT " + getNom() + "] - Vous avez joué " + carte.getNom() + " et avez ainsi gagné " + carte.getPoint() + " points, vous avez maintenant " + getNombrePointBatiment() + " points batiments" + ANSI_RESET);
                    break;
                case 2 :
                    addBoucliers(carte.getPoint());
                    if (!isIA()) System.out.println(ANSI_GREEN +"[CLIENT " + getNom() + "] - Vous avez joué " + carte.getNom() + " et avez ainsi gagné " + carte.getPoint() + " boucliers, vous avez maintenant " + getNbBoucliers() + " boucliers" + ANSI_RESET);
            }
        } else {
            if (isIA()) {
                defausserCarte(carte, indiceCarte);
            } else {
                System.out.println(ANSI_RED +"[CLIENT " + getNom() + "] - Il vous manque des ressources pour jouer la carte sélectionnée" + ANSI_RESET);
                tour(false);
            }
        }
    }

    /**
     * Permet de vérifier combien de vérifications de ressources sont à effectuer pour une carte donnée (plus
     * spécifiquement, le nombre de ressources différentes que coûte une carte)
     * @param c la carte à vérifier
     * @return le nombre de vérifications à effectuer
     */
    private int nombreDeVerifications(Carte c) {
        int totalVerifications = 0;
        for (Integer i : c.getCout().getRessources().values()) {
            if (i != 0) totalVerifications++;
        }
        return totalVerifications;
    }

    /**
     * Permet d'afficher le dialogue de choix d'un utilisateur (choix d'une action de jeu)
     * @param nouveauTour dans le cas où c'est un nouveau tour, un message supplémentaire est afficher pour faire
     *                    apparaître au joueur ses ressources et sa main
     */
    private void choixUtilisateur(boolean nouveauTour) {
        if (!aJoue && !isIA()) {
            Scanner sc = new Scanner(System.in);
            int reponseUtilisateur;
            int nbCartes = -1;

            if (nouveauTour) {
                System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Voici vos ressources" + ANSI_RESET);
                System.out.println((ANSI_GREEN + ressources.getRessourcesSansValeursZero() + ANSI_RESET).replace("{", "").replace("}", ""));
                System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Voici votre main" + ANSI_RESET);
                for (int i = 0; i < getMain().getCartes().size(); i++) {
                    System.out.println(ANSI_GREEN + i + " - " + getMain().getCartes().get(i) + ANSI_YELLOW);
                }
            }

            do {
                reponseUtilisateur = Integer.parseInt(lireEntree(sc, ANSI_YELLOW + "[CLIENT " + getNom() + "] - Vous pouvez (choisir un numero) :\n1) Jouer une carte\n2) Defausser une carte" + ANSI_RESET));
            } while(reponseUtilisateur < 1 || reponseUtilisateur > 2);

            switch(reponseUtilisateur) {
                case 1:
                    while (nbCartes < 0 || nbCartes > getMain().getCartes().size() - 1) {
                        System.out.println(ANSI_CYAN + "[CLIENT " + getNom() + "] - Veuillez saisir le numéro de la carte que vous voulez jouer :" + ANSI_RESET);
                        nbCartes = Integer.parseInt(sc.nextLine());
                    }
                    playCard(getMain().getCartes().get(nbCartes), getMain().getCartes().indexOf(getMain().getCartes().get(nbCartes)));
                    setAJoue(true);
                    break;
                case 2:
                    System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Voici votre main" + ANSI_RESET);
                    for (int i = 0; i < getMain().getCartes().size(); i++) {
                        System.out.println(ANSI_YELLOW + i + " - " + getMain().getCartes().get(i).getNom() + ANSI_YELLOW);
                    }

                    while (nbCartes < 0 || nbCartes > getMain().getCartes().size() - 1) {
                        System.out.println(ANSI_CYAN + "[CLIENT " + getNom() + "] - Veuillez saisir le numéro de la carte que vous voulez défausser :" + ANSI_RESET);
                        nbCartes = Integer.parseInt(sc.nextLine());
                    }
                    defausserCarte(getMain().getCartes().get(nbCartes), getMain().getCartes().indexOf(getMain().getCartes().get(nbCartes)));
                    setAJoue(true);
                    break;
            }
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
                "Nombre de pièces : /n" + this.ressources.getRessource("Gold") +
                "Nombre de points batiments : /n" + this.nombrePointBatiments;
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
        if (isIA()) System.out.println(ANSI_PURPLE + "[IA " + getNom() + "] - Connexion réussie" + ANSI_RESET);
        else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Connexion réussie" + ANSI_RESET);
        connexion.emit("envoiIdentification", getNom());
    }

    public void finPartie() {
        synchronized (attenteDeconnexion) {
            attenteDeconnexion.notify();
        }
    }

    /**
     * Emet un évènement au serveur pour lui indiquer que le joueur est prêt à jouer
     */
    public void readyToPlay() {
        if (!isIA()) System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Nouvelle main reçue" + ANSI_RESET);
        connexion.emit("playerReady");
    }

    /**
     * Permet de lire une entrée au clavier
     * @param sc le scanner utilisé pour lire les entrées clavier
     * @param message le message que l'on veut afficher avant de lire une entrée
     * @return ce qui a été lu par le scanner (ce que l'utilisateur a tappé)
     */
    private String lireEntree(Scanner sc, String message) {
        System.out.println(message);
        return sc.nextLine();
    }

    public void ajouterPointsVictoire(int points) {
        pointsVictoire += points;
        if (isIA()) System.out.println(ANSI_PURPLE + "[IA " + getNom() + "] - " + points + " points de victoire reçus pendant la phase de combat" + ANSI_RESET);
        else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - " + points + " points de victoire reçus pendant la phase de combat" + ANSI_RESET);
    }

    @Override
    public void run() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (isIA()) System.out.println(ANSI_PURPLE + "[IA " + getNom() + "] - Initialisation" + ANSI_RESET);
        else System.out.println(ANSI_YELLOW + "[CLIENT " + getNom() + "] - Initialisation" + ANSI_RESET);

        int port = 60001;

        connexion = new Connexion("http://" + "127.0.0.1" + ":" + port, this);
        this.seConnecter();
    }

}
