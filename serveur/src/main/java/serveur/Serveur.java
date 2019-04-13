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
    private ArrayList<SocketIOClient> clients;
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

    private static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) throws Exception {
        Serveur serveur = new Serveur();
    }

    private Serveur() throws Exception {
        int port = 60001;
        String ipAdress = "127.0.0.1";
        Configuration config = new Configuration();
        config.setHostname(ipAdress);
        config.setPort(port);
        clients = new ArrayList<>();
        clientsHashMap = new HashMap<>();
        clientsHashMapPointsMilitaires = new HashMap<>();

        // creation du serveur
        server = new SocketIOServer(config);
        server.start();

        System.out.println("[SERVEUR] - Serveur prêt en attente de connexions sur le port " + port);
        System.out.println("[SERVEUR] - Création d'un deck pour " + nbJoueurs + " joueurs");

        deck = new Deck(nbJoueurs);
        gestionnairePlateau = new GestionnairePlateau();

        ajoutEcouteurs();
    }

    public Serveur(int nbJoueurs, int nbJoueursIA, String adresse, int port) throws Exception {
        this.nbJoueurs = nbJoueurs;
        this.nbJoueursIA = nbJoueursIA;
        this.port = port;
        this.adresse = adresse;
        Configuration config = new Configuration();
        config.setHostname(this.adresse);
        config.setPort(this.port);
        clients = new ArrayList<>();
        clientsHashMap = new HashMap<>();
        clientsHashMapPointsMilitaires = new HashMap<>();

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
                clients.add(socketIOClient);
                System.out.println("[SERVEUR] - Nombre de clients : " + clients.size());

                if (clients.size() == nbJoueurs) {
                    System.out.println("[SERVEUR] - Tous les joueurs sont présents, la partie peut commencer");
                    distribuerJeu();
                } else {
                    System.out.println("[SERVEUR] - La partie commencera quand " + nbJoueurs + " seront connectés, actuellement " + clients.size() + "/" + nbJoueurs);
                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Déconnexion de " + socketIOClient.getRemoteAddress());
                clients.remove(socketIOClient);
                clientsHashMap.remove(socketIOClient);
            }
        });

        server.addEventListener("jeu", ArrayList.class, new DataListener<ArrayList>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ArrayList arrayList, AckRequest ackRequest) throws Exception {
                String nomJoueur = (String) arrayList.get(0);
                String nomCarteJouee = (String) arrayList.get(1);
                int indiceCarte = (int) arrayList.get(2);
                int typeInteraction = (int) arrayList.get(3);

                Carte carteTemp;
                Object objCarte = Class.forName(nomCarteJouee).newInstance();
                carteTemp = (Carte) objCarte;

                switch(typeInteraction) {
                    case 1: // carte defaussée
                        System.out.println("[SERVEUR] - Carte défaussée (" + carteTemp + ") par " + nomJoueur);
                        clientsHashMap.get(socketIOClient).getCartes().remove(indiceCarte);
                        nbJoueursJoues++;
                        socketIOClient.sendEvent("confirmationCarteDefaussee");
                        verifierFinTour();
                        break;
                    case 2: // carte jouée
                        System.out.println("[SERVEUR] - Carte jouée (" + carteTemp + ") par " + nomJoueur);
                        clientsHashMap.get(socketIOClient).getCartes().remove(indiceCarte);
                        nbJoueursJoues++;
                        verifierFinTour();
                        break;
                }
            }
        });

        server.addEventListener("playerReady", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                //Ici faire tous les tests pour savoir si tous les joueurs sont prets
                nbJoueurPrets++;
                if (nbJoueurPrets == clients.size()) {
                    for (SocketIOClient c : clients) {
                        int[] payload = new int[2];
                        payload[0] = numeroAge;
                        payload[1] = numeroTour;
                        c.sendEvent("turn", (Object) payload);
                    }
                }
            }
        });

        server.addEventListener("envoyerPointsMilitaire", int.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Integer integer, AckRequest ackRequest) throws Exception {
                if (clientsHashMapPointsMilitaires.containsKey(socketIOClient)) {
                    clientsHashMapPointsMilitaires.put(socketIOClient, integer);
                }
            }
        });
    }

    private void demanderMainsJoueurs() {
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
            case 1: current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes-totalCombatsPerdu); //+1 points en age 1
            case 2: current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes*3 - totalCombatsPerdu); //+3 points en age 2
            case 3: current.sendEvent("ajouterPointsVictoire", totalCombatsGagnes*5 - totalCombatsPerdu); //+5 points en age 3
        }
    }

    private void verifierFinTour() {
        if (nbJoueursJoues == clients.size()) {
            if (numeroTour == 6) {
                System.out.println("[SERVEUR] - Le sixième tour a été atteint, début de la phase de combat");
                demanderMainsJoueurs();

                int hashSize = clientsHashMapPointsMilitaires.size();
                int lastElementIndex = hashSize-1;
                int currentIndex = 0;

                List<Integer> pointsCombats = new ArrayList<Integer>(clientsHashMapPointsMilitaires.values());
                List<SocketIOClient> clients = new ArrayList<SocketIOClient >(clientsHashMapPointsMilitaires.keySet());

                for (SocketIOClient c : clientsHashMapPointsMilitaires.keySet()) {
                    if (currentIndex == 0) {
                        // Cas ou c'est le premier
                        calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(currentIndex+1), pointsCombats.get(lastElementIndex), clients.get(currentIndex));
                        currentIndex++;
                    } else if (currentIndex == lastElementIndex) {
                        // Cas ou c'est le dernier
                        calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(0), pointsCombats.get(currentIndex-1), clients.get(currentIndex));
                        currentIndex++;
                    } else {
                        calculEtEnvoiePointsVictoires(pointsCombats.get(currentIndex), pointsCombats.get(currentIndex+1), pointsCombats.get(currentIndex-1), clients.get(currentIndex));
                        currentIndex++;
                    }
                }

                numeroAge++;
                numeroTour = 0;
            }
            numeroTour++;
            System.out.println("[SERVEUR] - Tous les joueurs ont joué, début du tour " + numeroTour);
            nbJoueursJoues = 0;
            if (numeroAge % 2 != 0) clientsHashMap = permuter(true);
            for (SocketIOClient c : clientsHashMap.keySet()) {
                ArrayList<String> typesCartes = new ArrayList<>();
                for (int i = 0; i < clientsHashMap.get(c).getCartes().size(); i++) {
                    typesCartes.add(clientsHashMap.get(c).getCartes().get(i).getClass().getName());
                }
                int[] payload = new int[2];
                payload[0] = numeroAge;
                payload[1] = numeroTour;
                c.sendEvent("envoiMain", typesCartes);
                c.sendEvent("finTour", (Object) payload);
            }
        } else {
            System.out.println("[SERVEUR] - AGE " + numeroAge + " - TOUR " + numeroTour + " : " + nbJoueursJoues + " sur " + nbJoueurs + " ont joués, le tour suivant commencera quand tout le monde aura joué");
        }
    }

    // Fonction appelée pour distribuer cartes et plateaux
    public void distribuerJeu() {
        for (SocketIOClient c : clients) {
            // System.out.println("[SERVEUR] - Envoi d'un plateau au client " + c.getRemoteAddress());
            Plateau p = gestionnairePlateau.RandomPlateau();
            c.sendEvent("envoiPlateau", p.getClass().getName());

            // System.out.println("[SERVEUR] - Envoi d'une main au client " + c.getRemoteAddress());
            Main main = new Main(deck.genererMain());
            ArrayList<String> typesCartes = new ArrayList<>();
            for (int i = 0; i < main.getCartes().size(); i++) {
                typesCartes.add(main.getCartes().get(i).getClass().getName());
            }
            c.sendEvent("envoiMain", typesCartes);
            // System.out.println("[SERVEUR] - Nombre de cartes restantes dans le deck : " + deck.getDeck().size());
            clientsHashMap.put(c, main);
            clientsHashMapPointsMilitaires.put(c, 0);
        }
    }

    /**
     * Permet de permutter les mains des joueurs
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
