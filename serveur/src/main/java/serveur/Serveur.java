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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serveur {

    private SocketIOServer server;
    private HashMap<SocketIOClient, Main> clientsHashMap;
    private HashMap<SocketIOClient, Integer> clientsHashMapPointsMilitaires;
    //private ArrayList<SocketIOClient> clients;
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
    private final Object key = new Object();
    private HashMap<SocketIOClient, Tour> jeuTour;

    private static final String ANSI_RESET = "\u001B[0m";

    public Serveur(int nbJoueurs, int nbJoueursIA, String adresse, int port) throws Exception {
        this.nbJoueurs = nbJoueurs;
        this.nbJoueursIA = nbJoueursIA;
        this.port = port;
        this.adresse = adresse;
        Configuration config = new Configuration();
        config.setHostname(this.adresse);
        config.setPort(this.port);
        //clients = new ArrayList<>();
        clientsHashMap = new HashMap<>();
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


                synchronized (key) {
                    nbJoueursJoues++;
                    if (nbJoueursJoues == clientsHashMap.size()) {
                        System.out.println("Tous les joueurs ont jouée");

                        for (SocketIOClient current : jeuTour.keySet()) {
                            System.out.println("Carte indice " + jeuTour.get(current).getIndice() + " va etre retirée");
                            switch (jeuTour.get(current).getTypeInteraction()) {
                                case 1: // carte defaussée
                                    System.out.println("[SERVEUR] - Carte défaussée (" + jeuTour.get(current).getCarteJouee() + ") par " + jeuTour.get(current).getNomJoueur());

                                    System.out.println("Main avant \n" + clientsHashMap.get(current).getCartes());
                                    clientsHashMap.get(current).getCartes().remove(jeuTour.get(current).getIndice());
                                    System.out.println("Main après \n" + clientsHashMap.get(current).getCartes());

                                    socketIOClient.sendEvent("debug");
                                    //socketIOClient.sendEvent("confirmationCarteDefaussee");
                                    //verifierFinTour();
                                    break;
                                case 2: // carte jouée
                                    System.out.println("[SERVEUR] - Carte jouée (" + jeuTour.get(current).getCarteJouee() + ") par " + jeuTour.get(current).getNomJoueur());

                                    System.out.println("Main avant \n" + clientsHashMap.get(current).getCartes());
                                    clientsHashMap.get(socketIOClient).getCartes().remove(jeuTour.get(current).getIndice());
                                    System.out.println("Main après \n" + clientsHashMap.get(current).getCartes());

                                    socketIOClient.sendEvent("debug");
                                    //verifierFinTour();
                                    break;
                            }
                            System.out.println("-------------------------------------");
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

        Main main = new Main(deck.genererMain());
        ArrayList<String> typesCartes = generateTypesCartes(main);


        for (SocketIOClient client : clientsHashMap.keySet()) {
            int[] payload = new int[2];
            payload[0] = numeroAge;
            payload[1] = numeroTour;
            client.sendEvent("envoiMain", typesCartes);
            //client.sendEvent("finTour", (Object) payload);
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
            // System.out.println("[SERVEUR] - Nombre de cartes restantes dans le deck : " + deck.getDeck().size());
            clientsHashMap.replace(c, null, main);
            clientsHashMapPointsMilitaires.put(c, 0);
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
        distribuerMains();
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
