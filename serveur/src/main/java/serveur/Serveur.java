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
    private ArrayList<SocketIOClient> clients;
    private Deck deck;
    private GestionnairePlateau gestionnairePlateau;
    private String adresse;
    private int nbJoueurs;
    private int nbJoueursIA;
    private int port;
    private int nbJoueursJoues = 0;
    private int nbJoueurPrets = 0;
    private int numeroTour = 0;

    public static void main(String[] args) throws Exception {
        Serveur serveur = new Serveur();
    }

    public Serveur() throws Exception {
        //int nbJoueurs = listeClients.size();
        int port = 60001;
        String ipAdress = "127.0.0.1";
        Configuration config = new Configuration();
        config.setHostname(ipAdress);
        config.setPort(port);
        clients = new ArrayList<>();
        clientsHashMap = new HashMap<>();

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
            }
        });

        server.addEventListener("carteJouee", ArrayList.class, new DataListener<ArrayList>() {
            @Override
            public void onData(SocketIOClient socketIOClient, ArrayList arrayList, AckRequest ackRequest) throws Exception {
                String nomJoueur = (String) arrayList.get(0);
                String nomCarteJouee = (String) arrayList.get(1);

                Carte carteTemp;
                Object objCarte = Class.forName(nomCarteJouee).newInstance();
                carteTemp = (Carte) objCarte;
                System.out.println("[SERVEUR] - Carte jouée (" + carteTemp + ") par " + nomJoueur);
                nbJoueursJoues++;

                if (nbJoueursJoues == clients.size()) {
                    System.out.println("[SERVEUR] - Tous les joueurs ont joués, début du tour suivant");
                    numeroTour++;
                    nbJoueursJoues = 0;
                    for (SocketIOClient c : clients) {
                        c.sendEvent("finTour");
                    }
                } else {
                    System.out.println("[SERVEUR] - TOUR " + numeroTour + " : " + nbJoueursJoues + " sur " + nbJoueurs + " ont joués, le tour suivant commencera quand tout le monde aura joué");
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
                        c.sendEvent("turn");
                    }
                }
            }
        });
    }

    public void stop() {
        server.stop();
    }

    // Fonction appelée pour distribuer cartes et plateaux
    public void distribuerJeu() {
        for (SocketIOClient c : clients) {
            System.out.println("[SERVEUR] - Envoi d'un plateau au client " + c.getRemoteAddress());
            Plateau p = gestionnairePlateau.RandomPlateau();
            c.sendEvent("envoiPlateau", p.getClass().getName());

            System.out.println("[SERVEUR] - Envoi d'une main au client " + c.getRemoteAddress());
            Main main = new Main(deck.genererMain());
            ArrayList<String> typesCartes = new ArrayList<>();
            for (int i = 0; i < main.getCartes().size(); i++) {
                typesCartes.add(main.getCartes().get(i).getClass().getName());
            }
            c.sendEvent("envoiMain", typesCartes);
            System.out.println("[SERVEUR] - Nombre de cartes restantes dans le deck : " + deck.getDeck().size());
            clientsHashMap.put(c, main);
        }
        permuter(true);
        System.out.println(clientsHashMap);
    }

    /**
     * Permet de permutter les mains des joueurs
     * @param sens true signifie que les cartes vont être permutées vers la gauche et false vers la droite
     */
    private void permuter(boolean sens) {
        System.out.println("================== AVANT PERMUTATION ==================");
        System.out.println(clientsHashMap);
        System.out.println("=======================================================");
        List<Map.Entry<SocketIOClient, Main>> entrees = new ArrayList<>(clientsHashMap.entrySet());
            if (sens) { // vers la gauche
            for (int i = 0; i < clientsHashMap.entrySet().size(); i++) {
                System.out.println(entrees);
            }
        } else { // vers la droite

        }
        System.out.println("================== APRES PERMUTATION ==================");
        System.out.println(clientsHashMap);
        System.out.println("=======================================================");
    }
}
