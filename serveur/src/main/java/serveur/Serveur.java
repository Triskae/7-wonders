package serveur;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class Serveur {

    private SocketIOServer server;
    private ArrayList<SocketIOClient> clients;

    public static void main(String[] args) throws IOException {
        new Serveur();
    }

    private Serveur() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrer un port à utiliser pour le serveur (60001 recommandé) : ");
        int port = scanner.nextInt();
        scanner.close();
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
                socketIOClient.sendEvent("confirmationConnexion");
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("[SERVEUR] - Déconnexion de " + socketIOClient.getRemoteAddress());
                clients.remove(socketIOClient);
            }
        });

        server.start();

        server.addEventListener("envoieObjet", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Object o, AckRequest ackRequest) throws Exception {
                System.out.println("[SERVEUR] - " + o + " reçu");
//                for (SocketIOClient s : clients) {
//                    s.sendEvent("coucou", new Coup());
//                }
            }
        });

        server.addEventListener("testObject", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Object o, AckRequest ackRequest) throws Exception {
                System.out.println(o);
            }
        });

    }

    public void stop() {
        server.stop();
    }
}
