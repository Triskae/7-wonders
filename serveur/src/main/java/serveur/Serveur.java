package serveur;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import commun.Deck;
import commun.Main;

import java.net.InetAddress;
import java.util.ArrayList;

public class Serveur {

    private SocketIOServer server;
    private ArrayList<SocketIOClient> clients;
    private Deck deck;

    public static void main(String[] args) throws Exception {
        new Serveur();
    }

    private Serveur() throws Exception {
        int nbJoueurs = 3;
        int port = 60001;
        InetAddress inetAddress = InetAddress.getLocalHost();
        String ipAdress = inetAddress.getHostAddress();
        Configuration config = new Configuration();
        config.setHostname(ipAdress);
        config.setPort(port);
        clients = new ArrayList<>();

        // creation du serveur
        server = new SocketIOServer(config);

        server.addConnectListener(new ConnectListener() {
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Connexion de " + socketIOClient.getRemoteAddress());
                clients.add(socketIOClient);
                System.out.println("[SERVEUR] - Nombre de clients : " + clients.size());
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

        server.start();

        System.out.println("[SERVEUR] - Serveur prêt en attente de connexions sur le port " + port);
        System.out.println("[SERVEUR] - Création d'un deck pour " + nbJoueurs + " joueurs");
        deck = new Deck(nbJoueurs);
    }

    public void stop() {
        server.stop();
    }
}
