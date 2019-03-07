package serveur;

import client.Client;
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
import java.util.EventListener;

public class Serveur {

    private SocketIOServer server;
    private ArrayList<SocketIOClient> clients;
    private Deck deck;
    private GestionnairePlateau gestionnairePlateau;

    public Serveur(ArrayList<Client> listeClients) throws Exception {
        int nbJoueurs = listeClients.size();
        int port = 60001;
        String ipAdress = "127.0.0.1";
        Configuration config = new Configuration();
        config.setHostname(ipAdress);
        config.setPort(port);
        clients = new ArrayList<>();

        // creation du serveur
        server = new SocketIOServer(config);
        server.start();

        System.out.println("[SERVEUR] - Serveur prêt en attente de connexions sur le port " + port);
        System.out.println("[SERVEUR] - Création d'un deck pour " + nbJoueurs + " joueurs");


        deck = new Deck(nbJoueurs);
        gestionnairePlateau = new GestionnairePlateau();

        /*
            Tous les listeners du serveur.
         */

        server.addConnectListener(new ConnectListener() {
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Connexion de " + socketIOClient.getRemoteAddress());
                clients.add(socketIOClient);
                System.out.println("[SERVEUR] - Nombre de clients : " + clients.size());

                System.out.println("[SERVEUR] - Envoi d'un plateau au client " + socketIOClient.getRemoteAddress());
                Plateau p = gestionnairePlateau.RandomPlateau();
                socketIOClient.sendEvent("envoiPlateau", p.getClass().getName());

                System.out.println("[SERVEUR] - Envoi d'une main au client " + socketIOClient.getRemoteAddress());
                Main main = new Main(deck.genererMain());
                ArrayList<String> typesCartes = new ArrayList<>();
                for (int i = 0; i < main.getCartes().size(); i++) {
                    typesCartes.add(main.getCartes().get(i).getClass().getName());
                }
                socketIOClient.sendEvent("envoiMain", typesCartes);
                System.out.println("[SERVEUR] - Nombre de cartes restantes dans le deck : " + deck.getDeck().size());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Déconnexion de " + socketIOClient.getRemoteAddress());
                clients.remove(socketIOClient);
            }
        });

        server.addEventListener("carteJouee", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String carte, AckRequest ackRequest) throws Exception {
                Carte carteTemp;
                Object objCarte = Class.forName(carte).newInstance();
                carteTemp = (Carte) objCarte;
                System.out.println("-- SERVEUR CARTE JOUÉE-- " + carteTemp);
            }
        });

        server.addEventListener("playerReady", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                //Ici faire tous les tests pour savoir si tous les joueurs sont prets
                socketIOClient.sendEvent("turn");
            }
        });
    }

    public void stop() {
        server.stop();
    }
}
