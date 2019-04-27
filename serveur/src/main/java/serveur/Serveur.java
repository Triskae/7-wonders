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
import commun.cartes.Carte;
import commun.plateaux.GestionnairePlateau;
import commun.plateaux.Plateau;

import java.util.*;

public class Serveur {

    private SocketIOServer server;
    private HashMap<SocketIOClient, Main> clientsHashMap;
    private HashMap<SocketIOClient, String> identificationsClients;
    private HashMap<SocketIOClient, Boolean> statusDeJeu;
    private HashMap<SocketIOClient, Integer> clientsHashMapPointsMilitaires;
    private Deck deck;
    private GestionnairePlateau gestionnairePlateau;
    private String adresse;
    private int nbJoueurs;
    private int nbJoueursIA;
    private int port;
    private int nbJoueursJoues = 0;
    private int nbJoueurPrets = 0;
    private int numeroTour = 1;
    private int numeroAge = 1;
    private int clientNbRepondu = 0;
    private int nbMainsDepartRecues = 0;
    private int nbPlateauRecus = 0;
    private HashMap<SocketIOClient, Tour> jeuTour;

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
        identificationsClients = new HashMap<>();
        clientsHashMap = new HashMap<>();
        statusDeJeu = new HashMap<>();
        clientsHashMapPointsMilitaires = new HashMap<>();
        jeuTour = new HashMap<>();

        server = new SocketIOServer(config);
        server.start();

        System.out.println("[SERVEUR] - Serveur prêt en attente de connexions sur le port " + this.port);
        System.out.println("[SERVEUR] - Création d'un deck pour " + this.nbJoueurs + " joueurs");

        deck = new Deck(this.nbJoueurs);
        gestionnairePlateau = new GestionnairePlateau();

        ajoutEcouteurs();
    }

    private void ajoutEcouteurs() {
        server.addConnectListener(new ConnectListener() {


            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Connexion de " + socketIOClient.getRemoteAddress());
                clientsHashMap.put(socketIOClient, null);
                identificationsClients.put(socketIOClient, null);
                statusDeJeu.put(socketIOClient, false);
                System.out.println("[SERVEUR] - Nombre de clients : " + clientsHashMap.size());

                if (clientsHashMap.size() == nbJoueurs) {
                    System.out.println("[SERVEUR] - Tous les joueurs sont présents, la partie peut commencer");
                    distribuerJeu();
                } else {
                    System.out.println("[SERVEUR] - La partie commencera quand " + nbJoueurs + " seront connectés, actuellement " + clientsHashMap.size() + "/" + nbJoueurs);
                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Déconnexion de " + socketIOClient.getRemoteAddress());
                clientsHashMap.remove(socketIOClient);
            }
        });

        server.addEventListener("playerReady", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) {
                nbJoueurPrets++;
                if (nbJoueurPrets == clientsHashMap.size()) {
                    nbJoueurPrets = 0;
                    startTurn();
                }
            }
        });


        server.addEventListener("jeu", ArrayList.class, new DataListener<ArrayList>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ArrayList arrayList, AckRequest ackRequest) {
                String nomJoueur = (String) arrayList.get(0);
                String nomCarteJouee = (String) arrayList.get(1);
                int indiceCarte = (int) arrayList.get(2);
                int typeInteraction = (int) arrayList.get(3);

                try {
                    Carte carteTemp = (Carte) Class.forName(nomCarteJouee).newInstance();
                    jeuTour.put(socketIOClient, new Tour(typeInteraction, carteTemp, nomJoueur, indiceCarte));

                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                nbJoueursJoues++;
                if (nbJoueursJoues == clientsHashMap.size()) {
                    for (SocketIOClient current : jeuTour.keySet()) {
                        System.out.println("Carte indice " + jeuTour.get(current).getIndice() + " va etre retirée");
                        switch (jeuTour.get(current).getTypeInteraction()) {
                            case 1: // carte defaussée
                                System.out.println("[SERVEUR] - Carte défaussée (" + jeuTour.get(current).getCarteJouee() + ") par " + jeuTour.get(current).getNomJoueur());

                                // System.out.println("Main de " + jeuTour.get(current).getNomJoueur() + " avant \n" + clientsHashMap.get(current).getCartes());
                                clientsHashMap.get(current).getCartes().remove(jeuTour.get(current).getIndice());
                                // System.out.println("Main de " + jeuTour.get(current).getNomJoueur() + " après \n" + clientsHashMap.get(current).getCartes());
                                verifierFinTour();
                                break;
                            case 2: // carte jouée
                                System.out.println("[SERVEUR] - Carte jouée (" + jeuTour.get(current).getCarteJouee() + ") par " + jeuTour.get(current).getNomJoueur());

                                // System.out.println("Main de " + jeuTour.get(current).getNomJoueur() + " avant \n" + clientsHashMap.get(current).getCartes());
                                clientsHashMap.get(current).getCartes().remove(jeuTour.get(current).getIndice());
                                // System.out.println("Main de " + jeuTour.get(current).getNomJoueur() + " après \n" + clientsHashMap.get(current).getCartes());
                                verifierFinTour();
                                break;
                        }
                    }
                }
            }
        });

        server.addEventListener("envoyerPointsMilitaire", int.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Integer integer, AckRequest ackRequest) {
                if (clientsHashMapPointsMilitaires.containsKey(socketIOClient)) {
                    clientsHashMapPointsMilitaires.put(socketIOClient, integer);
                    clientNbRepondu++;
                }

                if (clientNbRepondu == nbJoueurs) {
                    phaseCombat();
                    // IL FAUT RESET LE NOMBRE DE CLIENT REPONDU A CHAQUE FIN D'AGE
                }
            }
        });

        server.addEventListener("confirmationReceptionPlateau", ArrayList.class, new DataListener<ArrayList>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ArrayList arrayList, AckRequest ackRequest) throws Exception {
                System.out.println("[SERVEUR] - " + arrayList.get(0) + " a bien reçu son plateau, son status de jeu est " + arrayList.get(1));
                identificationsClients.replace(socketIOClient, (String) arrayList.get(0));
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

                    if (indiceCarte > clientsHashMap.get(socketIOClient).getCartes().size() - 1) {
                        System.out.println(ANSI_RED + "[SERVEUR] - Problème de synchronisation!" + ANSI_RESET);
                    }

                    if (typeAction == 1) {
                        System.out.println("[SERVEUR] - Réception de la demande de défausse de la carte " + clientsHashMap.get(socketIOClient).getCartes().get(indiceCarte) + " par " + identificationsClients.get(socketIOClient));
                    } else {
                        System.out.println("[SERVEUR] - Réception de la demande de jeu de la carte " + clientsHashMap.get(socketIOClient).getCartes().get(indiceCarte) + " par " + identificationsClients.get(socketIOClient));
                    }

                    System.out.println("[SERVEUR] - Suppression de la carte " + clientsHashMap.get(socketIOClient).getCartes().get(indiceCarte) + " de la main du joueur " + identificationsClients.get(socketIOClient));
                    clientsHashMap.get(socketIOClient).getCartes().remove(indiceCarte);
                    System.out.println("[SERVEUR] - Nouvelle main (avant permutation) pour " + identificationsClients.get(socketIOClient) + "\n" + clientsHashMap.get(socketIOClient));

                    statusDeJeu.replace(socketIOClient, true);
                    verifToutLeMondeAJoue();
                } else {
                    System.out.println(ANSI_RED + "[SERVEUR] - " + identificationsClients.get(socketIOClient) + " a  déjà joué et retente de jouer!" + ANSI_RESET);
                }
            }
        });
    }

    private void verifToutLeMondeAJoue() {
        if (numeroTour == 6) {
            System.out.println("[SERVEUR] - Tour 6 atteint");
        } else {
            int nbAJoue = 0;
            // Calcul du nombre de joueurs qui ont déjà joués
            for (SocketIOClient c : statusDeJeu.keySet()) {
                if (statusDeJeu.get(c)) nbAJoue++;
            }

            if (nbAJoue == nbJoueurs) {
                for (SocketIOClient c : statusDeJeu.keySet()) {
                    statusDeJeu.replace(c, false);
                }

                clientsHashMap = permuter(true);

//                System.out.println("[SERVEUR] - Début permutation des mains : rappel mains actuelles\n");
//                for (SocketIOClient c : clientsHashMap.keySet()) {
//                    System.out.println("Pour " + identificationsClients.get(c));
//                    System.out.println(clientsHashMap.get(c));
//                }
//                clientsHashMap = permuter(true);
//                System.out.println("[SERVEUR] - Fin permutation des mains : nouvelles mains\n");
//                for (SocketIOClient c : clientsHashMap.keySet()) {
//                    System.out.println("Pour " + identificationsClients.get(c));
//                    System.out.println(clientsHashMap.get(c));
//                }

                numeroTour++;
                nbMainsDepartRecues = 0;
                envoyerNouvellesMains();
            } else {
                System.out.println("[SERVEUR] - Tous les joueurs n'ont pas encore joués");
            }
        }
    }

    private void verifReceptionPlateaux() {
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
        for (SocketIOClient c : clientsHashMap.keySet()) {
            statusDeJeu.replace(c, false);
            c.sendEvent("nouveauTour");
        }
        System.out.println("========================== DEBUT DU TOUR " + numeroTour + " DE L'AGE " + numeroAge + " ==========================");
    }

    private void envoyerMainsDeDepart() {
        for (SocketIOClient c : clientsHashMap.keySet()) {
            Main main = new Main(deck.genererMain());
            clientsHashMap.replace(c, main);
            ArrayList<String> typesCartes = generateTypesCartes(main);
            System.out.println("[SERVEUR] - Envoi de la main de départ\n" + main + "\nà " + identificationsClients.get(c));
            c.sendEvent("envoyerMain", typesCartes);
        }
    }

    private void envoyerNouvellesMains() {
        for (SocketIOClient c : clientsHashMap.keySet()) {
            Main main = clientsHashMap.get(c);
            ArrayList<String> typesCartes = generateTypesCartes(main);
            System.out.println("[SERVEUR] - Envoi de la nouvelle main\n" + main + "\nà " + identificationsClients.get(c));
            c.sendEvent("envoyerMain", typesCartes);
        }
    }

    private void startTurn() {
        for (SocketIOClient c : clientsHashMap.keySet()) {
            int[] payload = new int[2];
            payload[0] = numeroAge;
            payload[1] = numeroTour;
            c.sendEvent("turn", (Object) payload);
        }
    }


    private void demanderPointsMilitaire() {
        for (SocketIOClient c : clientsHashMapPointsMilitaires.keySet()) {
            c.sendEvent("demanderPointsMilitaire");
        }
    }

    private void calculEtEnvoiePointsVictoires(int pointsCourants, int pointsDroite, int pointsGauche, SocketIOClient current) {
        int totalCombatsGagnes = 0;
        int totalCombatsPerdu = 0;

        if (pointsCourants > pointsDroite) totalCombatsGagnes++;
        if (pointsCourants > pointsGauche) totalCombatsGagnes++;
        if (pointsCourants < pointsDroite) totalCombatsPerdu++;
        if (pointsCourants < pointsGauche) totalCombatsPerdu++;

        switch (numeroAge) {
            case 1:
                current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes - totalCombatsPerdu);
                break; //+1 points en age 1
            case 2:
                current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes * 3 - totalCombatsPerdu);
                break; //+3 points en age 2
            case 3:
                current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes * 5 - totalCombatsPerdu);
                break;//+5 points en age 3
        }
        startTurn();
    }

    private void phaseCombat() {
        System.out.println("[SERVEUR] - Le sixième tour a été atteint, début de la phase de combat");
        int hashSize = clientsHashMapPointsMilitaires.size();

        int lastElementIndex = hashSize - 1;
        int currentIndex = 0;

        List<Integer> pointsCombats = new ArrayList<Integer>(clientsHashMapPointsMilitaires.values());
        List<SocketIOClient> clients = new ArrayList<SocketIOClient>(clientsHashMapPointsMilitaires.keySet());

        for (SocketIOClient c : clientsHashMapPointsMilitaires.keySet()) {
            if (currentIndex == 0) {
                // Cas ou c'est le premier
                calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(currentIndex + 1), pointsCombats.get(lastElementIndex), clients.get(currentIndex));
                currentIndex++;
            } else if (currentIndex == lastElementIndex) {
                // Cas ou c'est le dernier
                calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(0), pointsCombats.get(currentIndex - 1), clients.get(currentIndex));
                currentIndex++;
            } else {
                calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(currentIndex + 1), pointsCombats.get(currentIndex - 1), clients.get(currentIndex));
                currentIndex++;
            }
        }
        initMainsAge();
    }

    private void initMainsAge() {
        numeroAge++;
        numeroTour = 1;
        nbJoueursJoues = 0;

        try {
            //TODO METTRE A JOUR CONSTRUCTEUR DE DECK (METTRE AGE EN PARAM)
            this.deck = new Deck(this.nbJoueurs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (SocketIOClient client : clientsHashMap.keySet()) {
            Main main = new Main(deck.genererMain());
            ArrayList<String> typesCartes = generateTypesCartes(main);
            client.sendEvent("envoiMain", typesCartes);
        }
    }

    private void verifierFinTour() {
        if (nbJoueursJoues == clientsHashMap.size()) {
            if (numeroTour == 6) {
                demanderPointsMilitaire();
            } else {
                numeroTour++;
                System.out.println("[SERVEUR] - Tous les joueurs ont joué, début du tour " + numeroTour);
                nbJoueursJoues = 0;
                if (numeroAge % 2 != 0) clientsHashMap = permuter(true);
                else clientsHashMap = permuter(false);
                for (SocketIOClient c : clientsHashMap.keySet()) {
                    ArrayList<String> typesCartes = generateTypesCartes(clientsHashMap.get(c));
                    int[] payload = new int[2];
                    payload[0] = numeroAge;
                    payload[1] = numeroTour;
                    c.sendEvent("envoiMain", typesCartes);
                    c.sendEvent("turn", (Object) payload);
                }
            }
        } else {
            System.out.println("[SERVEUR] - AGE " + numeroAge + " - TOUR " + numeroTour + " : " + nbJoueursJoues + " sur " + nbJoueurs + " ont joués, le tour suivant commencera quand tout le monde aura joué");
        }
    }

    /**
     * Permet de distribuer un plateau à chaque joueur
     */
    private void distribuerPlateau() {
        for (SocketIOClient c : clientsHashMap.keySet()) {
            Plateau p = gestionnairePlateau.RandomPlateau();
            c.sendEvent("envoiPlateau", p.getClass().getName());
        }
    }

    /**
     * Permet de distribuer une main à chaque joueur
     */
    private void distribuerMains() {
        for (SocketIOClient c : clientsHashMap.keySet()) {
            Main main = new Main(deck.genererMain());
            ArrayList<String> typesCartes = generateTypesCartes(main);
            c.sendEvent("envoiMain", typesCartes);
            clientsHashMap.replace(c, main);
            clientsHashMapPointsMilitaires.put(c, 0);
            System.out.println("");
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
     * Permet de distribuer un jeu complet (plateau + main)
     */
    private void distribuerJeu() {
        distribuerPlateau();
        // distribuerMains();
    }

    /**
     * Permet de permutter les mains des joueurs
     *
     * @param sens true signifie que les cartes vont être permutées vers la gauche et false vers la droite
     */
    private HashMap<SocketIOClient, Main> permuter(boolean sens) {
        HashMap<SocketIOClient, Main> nouvelleClientsHashMap = new HashMap<>();
        ArrayList<SocketIOClient> clientsTemp = new ArrayList<>();
        ArrayList<Main> mainsTemp = new ArrayList<>();

        for (Map.Entry<SocketIOClient, Main> entree : clientsHashMap.entrySet()) {
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

        for (int i = 0; i < clientsHashMap.size(); i++) {
            nouvelleClientsHashMap.put(clientsTemp.get(i), mainsTemp.get(i));
        }

        return nouvelleClientsHashMap;
    }
}
