package serveur;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import commun.Deck;
import commun.Main;
import commun.plateaux.GestionnairePlateau;
import commun.plateaux.Plateau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serveur {

    private SocketIOServer server;
    private HashMap<SocketIOClient, Plateau> clientsPlateaux;
    private HashMap<SocketIOClient, Main> clientsMains;
    private HashMap<SocketIOClient, String> identificationsClients;
    private HashMap<SocketIOClient, Boolean> statusDeJeu;
    private HashMap<SocketIOClient, Integer> clientsNbBoucliers;
    private HashMap<SocketIOClient, Integer> clientsNbPointsBatiments;
    private HashMap<SocketIOClient, Integer> resultatsPhaseCombat;
    private HashMap<SocketIOClient, Integer> clientsNbPieces;
    private Deck deck;
    private GestionnairePlateau gestionnairePlateau;
    private String adresse;
    private int nbJoueurs;
    private int nbJoueursIA;
    private int port;
    private int numeroTour = 1;
    private int numeroAge = 1;
    private int nbMainsDepartRecues = 0;
    private int nbPlateauRecus = 0;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    public Serveur(int nbJoueurs, int nbJoueursIA, String adresse, int port) throws Exception {
        this.nbJoueurs = nbJoueurs;
        this.nbJoueursIA = nbJoueursIA;
        this.port = port;
        this.adresse = adresse;
        Configuration config = new Configuration();
        config.setHostname(this.adresse);
        config.setPort(this.port);
        clientsPlateaux = new HashMap<>();
        identificationsClients = new HashMap<>();
        clientsMains = new HashMap<>();
        statusDeJeu = new HashMap<>();
        clientsNbBoucliers = new HashMap<>();
        clientsNbPointsBatiments = new HashMap<>();
        resultatsPhaseCombat = new HashMap<>();
        clientsNbPieces = new HashMap<>();

        deck = new Deck(this.nbJoueurs);
        gestionnairePlateau = new GestionnairePlateau();

        server = new SocketIOServer(config);
        server.start();

        System.out.println("[SERVEUR] - Création d'un deck pour " + this.nbJoueurs + " joueurs");

        ajoutEcouteurs();
    }

    private void ajoutEcouteurs() {
        server.addConnectListener(new ConnectListener() {


            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Connexion de " + socketIOClient.getRemoteAddress());
                clientsMains.put(socketIOClient, null);
                identificationsClients.put(socketIOClient, null);
                statusDeJeu.put(socketIOClient, false);
                clientsPlateaux.put(socketIOClient, null);
                clientsNbPointsBatiments.put(socketIOClient, 0);
                clientsNbBoucliers.put(socketIOClient, 0);
                resultatsPhaseCombat.put(socketIOClient, 0);
                clientsNbPieces.put(socketIOClient, 3);

                if (clientsMains.size() == nbJoueurs) {
                    System.out.println("[SERVEUR] - Tous les joueurs sont présents, la partie peut commencer");
                    distribuerPlateaux();
                } else {
                    System.out.println("[SERVEUR] - La partie commencera quand " + nbJoueurs + " seront connectés, actuellement " + clientsMains.size() + "/" + nbJoueurs);
                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Déconnexion de " + socketIOClient.getRemoteAddress());
                clientsMains.remove(socketIOClient);
            }
        });

        server.addEventListener("confirmationReceptionPlateau", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String identification, AckRequest ackRequest) throws Exception {
                System.out.println("[SERVEUR] - " + identification + " a bien reçu son plateau");
                identificationsClients.replace(socketIOClient, identification);
                nbPlateauRecus++;
                verifReceptionPlateaux();
            }
        });

        server.addEventListener("confirmationReceptionMain", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Object o, AckRequest ackRequest) throws Exception {
                System.out.println("[SERVEUR] - " + identificationsClients.get(socketIOClient) + " a bien reçu sa main");
                nbMainsDepartRecues++;
                verifReceptionMains();
            }
        });

        server.addEventListener("actionDeJeu", ArrayList.class, new DataListener<ArrayList>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ArrayList arrayList, AckRequest ackRequest) throws Exception {
                if (!statusDeJeu.get(socketIOClient)) {
                    int indiceCarte = (int) arrayList.get(0);
                    int typeAction = (int) arrayList.get(1);

                    if (indiceCarte > clientsMains.get(socketIOClient).getCartes().size() - 1) {
                        System.out.println(ANSI_RED + "[SERVEUR] - Problème de synchronisation!" + ANSI_RESET);
                    }

                    if (typeAction == 1) {
                        clientsNbPieces.replace(socketIOClient, clientsNbPieces.get(socketIOClient) + 3);
                        socketIOClient.sendEvent("confirmationCarteDefaussee", clientsNbPieces.get(socketIOClient));
                        System.out.println("[SERVEUR] - " + identificationsClients.get(socketIOClient) + " vient de défausser " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte) + " et vient ainsi de gagner 3 pièces, ce qui lui donne maintenant un total de " + clientsNbPieces.get(socketIOClient) + " pièces!");
                    } else {
                        clientsPlateaux.get(socketIOClient).ajouterCarteJouee(clientsMains.get(socketIOClient).getCartes().get(indiceCarte));
                        switch (clientsMains.get(socketIOClient).getCartes().get(indiceCarte).getType()) {
                            case 1:
                                clientsNbPointsBatiments.replace(socketIOClient, clientsNbPointsBatiments.get(socketIOClient) + clientsMains.get(socketIOClient).getCartes().get(indiceCarte).getPoint());
                                System.out.println("[SERVEUR] - " + identificationsClients.get(socketIOClient) + " vient de jouer " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte) + " et vient ainsi de gagner " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte).getPoint() + " points de bâtiment, ce qui lui donne maintenant un total de " + clientsNbPointsBatiments.get(socketIOClient) + " points de bâtiments!");
                                break;
                            case 2:
                                clientsNbBoucliers.replace(socketIOClient, clientsNbBoucliers.get(socketIOClient) + clientsMains.get(socketIOClient).getCartes().get(indiceCarte).getPoint());
                                System.out.println("[SERVEUR] - " + identificationsClients.get(socketIOClient) + " vient de jouer " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte) + " et vient ainsi de gagner " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte).getPoint() + " boucliers, ce qui lui donne maintenant un total de " + clientsNbBoucliers.get(socketIOClient) + " boucliers!");
                                break;
                            default:
                                System.out.println("[SERVEUR] - " + identificationsClients.get(socketIOClient) + " vient de jouer " + clientsMains.get(socketIOClient).getCartes().get(indiceCarte) + "!");
                        }

                    }

                    clientsMains.get(socketIOClient).getCartes().remove(indiceCarte);

                    statusDeJeu.replace(socketIOClient, true);
                    verifToutLeMondeAJoue();
                } else {
                    System.out.println(ANSI_RED + "[SERVEUR] - " + identificationsClients.get(socketIOClient) + " a  déjà joué et retente de jouer!" + ANSI_RESET);
                }
            }
        });
    }

    private void verifToutLeMondeAJoue() throws Exception {
        int nbAJoue = 0;
        // Calcul du nombre de joueurs qui ont déjà joués
        for (SocketIOClient c : statusDeJeu.keySet()) {
            if (statusDeJeu.get(c)) nbAJoue++;
        }

        if (nbAJoue == nbJoueurs && !(numeroTour == 6)) {
            for (SocketIOClient c : statusDeJeu.keySet()) {
                statusDeJeu.replace(c, false);
            }

            if (numeroAge % 2 != 0) clientsMains = permuter(true);
            else clientsMains = permuter(false);

            numeroTour++;
            nbMainsDepartRecues = 0;
            System.out.println("========================== FIN DU TOUR " + (numeroTour - 1) + " DE L'AGE " + numeroAge + " ==========================");
            envoyerNouvellesMains();
        } else if (nbAJoue == nbJoueurs) {
            phaseCombat();
        } else {
            System.out.println("[SERVEUR] - Tous les joueurs n'ont pas encore joués");
        }
    }

    private void verifReceptionPlateaux() throws Exception {
        if (nbPlateauRecus == nbJoueurs) {
            System.out.println("[SERVEUR] - Tous les joueurs ont reçus leur plateau, on peut envoyer les mains");
            envoyerMainsDeDepart();
        }
    }

    private void verifReceptionMains() {
        if (nbMainsDepartRecues == nbJoueurs) {
            System.out.println("[SERVEUR] - Tous les joueurs ont reçus leur main, on peut commencer à jouer");
            demarrerLeJeu();
        }
    }

    private void demarrerLeJeu() {
        for (SocketIOClient c : clientsMains.keySet()) {
            statusDeJeu.replace(c, false);
            c.sendEvent("nouveauTour");
        }
        System.out.println("========================== DEBUT DU TOUR " + numeroTour + " DE L'AGE " + numeroAge + " ==========================");
    }

    /**
     * Envoi de la main de départ à chaque joueur
     * Diffère de la méthode envoyerNouvellesMains par le fait qu'elle génére une nouvelle main
     */
    private void envoyerMainsDeDepart() {
        for (SocketIOClient c : clientsMains.keySet()) {
            Main main = new Main(deck.genererMain());
            clientsMains.replace(c, main);
            ArrayList<String> typesCartes = generateTypesCartes(main);
            // System.out.println("[SERVEUR] - Envoi de la main de départ\n" + main + "\nà " + identificationsClients.get(c));
            c.sendEvent("envoyerMain", typesCartes);
        }
    }

    /**
     * Envoi de la main aux joueur
     * Diffère de la méthode envoyerMainsDeDepart par le fait qu'elle ne génère pas de nouvelle main, elle utilise celles
     * qui se trouvent dans la HashMap clientsMain
     */
    private void envoyerNouvellesMains() {
        for (SocketIOClient c : clientsMains.keySet()) {
            Main main = clientsMains.get(c);
            ArrayList<String> typesCartes = generateTypesCartes(main);
            // System.out.println("[SERVEUR] - Envoi de la nouvelle main\n" + main + "\nà " + identificationsClients.get(c));
            c.sendEvent("envoyerMain", typesCartes);
        }
    }

    private void calculEtEnvoiePointsVictoires(int pointsCourants, int pointsDroite, int pointsGauche, SocketIOClient current) {
        int totalCombatsGagnes = 0;
        int totalCombatsPerdu = 0;

        if (pointsCourants > pointsDroite) totalCombatsGagnes++;
        if (pointsCourants > pointsGauche) totalCombatsGagnes++;
        if (pointsCourants < pointsDroite) totalCombatsPerdu++;
        if (pointsCourants < pointsGauche) totalCombatsPerdu++;

        int nbPointsGagnes = 0;

        switch (numeroAge) {
            case 1:
                nbPointsGagnes = totalCombatsGagnes - totalCombatsPerdu;
                break; // +1 point en age 1
            case 2:
                nbPointsGagnes = totalCombatsGagnes * 3 - totalCombatsPerdu;
                break; // +3 points en age 2
            case 3:
                nbPointsGagnes = totalCombatsGagnes * 5 - totalCombatsPerdu;
                break; // +5 points en age 3
        }

        resultatsPhaseCombat.replace(current, nbPointsGagnes);
        current.sendEvent("ajouterPointsVictoire", nbPointsGagnes);
    }

    private void phaseCombat() throws Exception {
        System.out.println("[SERVEUR] - Début de la phase de combat");
        int hashSize = clientsNbBoucliers.size();
        int lastElementIndex = hashSize - 1;
        int currentIndex = 0;

        List<Integer> boucliers = new ArrayList<>(clientsNbBoucliers.values());
        List<SocketIOClient> clients = new ArrayList<>(clientsNbBoucliers.keySet());

        for (SocketIOClient c : clientsNbBoucliers.keySet()) {
            if (currentIndex == 0) {
                // Cas ou c'est le premier
                calculEtEnvoiePointsVictoires(boucliers.get(currentIndex), boucliers.get(currentIndex + 1), boucliers.get(lastElementIndex), clients.get(currentIndex));
                currentIndex++;
            } else if (currentIndex == lastElementIndex) {
                // Cas ou c'est le dernier
                calculEtEnvoiePointsVictoires(boucliers.get(currentIndex), boucliers.get(0), boucliers.get(currentIndex - 1), clients.get(currentIndex));
                currentIndex++;
            } else {
                calculEtEnvoiePointsVictoires(boucliers.get(currentIndex), boucliers.get(currentIndex + 1), boucliers.get(currentIndex - 1), clients.get(currentIndex));
                currentIndex++;
            }
        }

        System.out.println("========================== RESULTATS DE LA PHASE DE COMBAT DE L'AGE " + numeroAge + " ==========================");
        for (SocketIOClient c : clientsMains.keySet()) {
            System.out.println(identificationsClients.get(c) + " a obtenu " + resultatsPhaseCombat.get(c) + " point(s) de victoire suite à la phase de combat!");
        }

        bilanTour();
    }

    private void bilanTour() throws Exception {
        System.out.println("========================== BILAN AGE " + numeroAge + " ==========================");
        for (SocketIOClient c : clientsMains.keySet()) {
            int pointsVictoire = 0;
//            System.out.println("Pts bâtiments " + clientsNbPointsBatiments.get(c));
//            System.out.println("Boucliers " + clientsNbBoucliers.get(c));
            pointsVictoire += clientsNbPointsBatiments.get(c);
            pointsVictoire += resultatsPhaseCombat.get(c);
            System.out.println(identificationsClients.get(c) + " a un total " + pointsVictoire + " points de victoire dont\n" + resultatsPhaseCombat.get(c) + " points obtenus pendant la phase de combat\n" + clientsNbPointsBatiments.get(c) + " points obtenus grâce aux bâtiments");
        }

        if (numeroAge == 3) {
            System.out.println("========================== L'AGE 3 A ETE ATTEINT, FIN DU JEU ==========================");
        } else {
            nbMainsDepartRecues = 0;
            numeroTour = 1;
            numeroAge++;
            deck = new Deck(nbJoueurs);
            envoyerMainsDeDepart();
        }
    }

    /**
     * Permet de distribuer un plateau à chaque joueur
     */
    private void distribuerPlateaux() {
        for (SocketIOClient c : clientsMains.keySet()) {
            Plateau p = gestionnairePlateau.RandomPlateau();
            clientsPlateaux.replace(c, p);
            c.sendEvent("envoiPlateau", p.getClass().getName());
        }
    }

    /**
     * Cette fonction créer une vue de la main à envoyer au client
     * Cette main est en faite List de nom de cartes qui sera recréé coté client avec la réflexion.
     *
     * @param main La main pour laquelle la vue va être créé
     * @return La list qui va contenir le nom de toutes les cartes de la mains
     */
    private ArrayList<String> generateTypesCartes(Main main) {
        ArrayList<String> typesCartes = new ArrayList<>();
        for (int i = 0; i < main.getCartes().size(); i++) {
            typesCartes.add(main.getCartes().get(i).getClass().getName());
        }
        return typesCartes;
    }

    /**
     * Permet de permutter les mains des joueurs
     * @param sens true signifie que les cartes vont être permutées vers la gauche et false vers la droite
     * @return la nouvelle HashMap contenant les SocketIOClient des clients ainsi que leur nouvelle main après permutation
     */
    private HashMap<SocketIOClient, Main> permuter(boolean sens) {
        HashMap<SocketIOClient, Main> nouvelleClientsHashMap = new HashMap<>();
        ArrayList<SocketIOClient> clientsTemp = new ArrayList<>();
        ArrayList<Main> mainsTemp = new ArrayList<>();
        for (Map.Entry<SocketIOClient, Main> entree : clientsMains.entrySet()) {
            clientsTemp.add(entree.getKey());
            mainsTemp.add(entree.getValue());
        }
        if (sens) {
            // permutation vers la gauche
            Main premiereMainTemp = mainsTemp.get(0);
            for (int i = 0; i < mainsTemp.size() - 1; i++) {
                mainsTemp.set(i, mainsTemp.get(i + 1));
            }
            mainsTemp.set(mainsTemp.size() - 1, premiereMainTemp);
        } else {
            // permutation vers la droite
            Main derniereMainTemp = mainsTemp.get(mainsTemp.size() - 1);
            for (int i = mainsTemp.size() - 1; i > 0; i--) {
                mainsTemp.set(i, mainsTemp.get(i - 1));
            }
            mainsTemp.set(0, derniereMainTemp);
        }
        for (int i = 0; i < clientsMains.size(); i++) {
            nouvelleClientsHashMap.put(clientsTemp.get(i), mainsTemp.get(i));
        }
        return nouvelleClientsHashMap;
    }
}
