package jeu;

import client.Client;
import serveur.Serveur;

import java.util.ArrayList;

public class Jeu {

    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        genererClients(3);
        Serveur serveur = new Serveur(clients);
    }

    private static void genererClients(int nbClients) throws InterruptedException {
        clients.add(new Client("Samuel"));
        clients.add(new Client("Filipe"));
        clients.add(new Client("Hugo"));

        for (Thread client : clients) {
            client.start();
            Thread.sleep(1000);
        }
    }
}
