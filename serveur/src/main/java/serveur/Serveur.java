package serveur;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import commun.Coup;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Serveur {

    private SocketIOServer server;
    private ArrayList<SocketIOClient> clients;

//    final Object attenteConnexion = new Object();
//    private int àTrouvé = 42;
//    Identification leClient ;
//    ArrayList<Coup> coups = new ArrayList<>();


    public static void main(String[] args) throws UnknownHostException {
        new Serveur();
    }


    public Serveur() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        int port = 60001;
        String ipAdress = inetAddress.getHostAddress();
        Configuration config = new Configuration();
        config.setHostname(ipAdress);
        config.setPort(port);
        clients = new ArrayList<>();

        // creation du serveur
        server = new SocketIOServer(config);
        server.start();
        server.addConnectListener(new ConnectListener() {
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("connexion de " + socketIOClient.getRemoteAddress());
                clients.add(socketIOClient);
                System.out.println(clients);
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                System.out.println("déconnexion de " + socketIOClient.getRemoteAddress());
                clients.remove(socketIOClient);
            }
        });

        server.addEventListener("envoieObjet", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Object o, AckRequest ackRequest) throws Exception {
                System.out.println("Recu depuis le client");
                System.out.println((String) o);
                for (SocketIOClient s : clients) {
                    s.sendEvent("coucou", new Coup());
                }
            }
        });
    }


    public void stop() {
        server.stop();
    }
}
