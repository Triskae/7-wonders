package jeu;

import client.Client;
import commun.Deck;

import java.util.ArrayList;

public class Jeu {

    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        genererClients(3);
    }

    private static void genererClients(int nbClients) throws InterruptedException {
        for (int i = 0; i < nbClients; i++) {
            clients.add(new Client("client" + i));
        }

        for (Thread client : clients) {
            client.start();
            Thread.sleep(1000);
        }
    }
}
