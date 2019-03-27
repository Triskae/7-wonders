package jeu;

import client.Client;
import serveur.Serveur;

import java.util.ArrayList;
import java.util.Scanner;

public class Jeu {

    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Veuillez entrer votre nom :");
        String nomJoueur = lireEntree();
        System.out.println(nomJoueur + ", voulez vous (entrer 1 ou 2) :\n" +
                "1) Créer une partie\n" +
                "2) Rejoindre une partie");
        String urlServeur;
        int portServeur;
        switch (lireEntree()) {
            case "1": // cas création d'une partie
                System.out.println("Veuillez entrer le nombre de joueur de la partie vous compris (entre 3 (maximum pour le moment) et 7) :");
                int nbJoueurs = Integer.parseInt(lireEntree());
                while (nbJoueurs < 3 || nbJoueurs > 7) {
                    nbJoueurs = Integer.parseInt(lireEntree());
                }
                System.out.println("Veuillez entrer le nombre de joueurs non-humains (" + (nbJoueurs - 1) + " au maximum) :");
                int nbJoueursIA = Integer.parseInt(lireEntree());
                if (nbJoueursIA > (nbJoueurs - 1)) {
                    nbJoueursIA = Integer.parseInt(lireEntree());
                }
                System.out.println("Veuillez entrer une adresse pour le serveur (sans le port, ne rien entrer pour l'adresse par défaut 127.0.0.1) :");
                String entreeURL = lireEntree();
                if (entreeURL.equals("")) urlServeur = "127.0.0.1";
                else urlServeur = entreeURL;
                System.out.println("Veuillez entrer un port pour le serveur (ne rien entrer pour le port par défaut 60001) :");
                String entreePort = lireEntree();
                if (entreePort.equals("")) portServeur = 60001;
                else portServeur = Integer.parseInt(entreePort);
                System.out.println("Création d'une partie pour " + nbJoueurs + " joueurs dont " + nbJoueursIA + " joueurs IA");
                new Serveur(nbJoueurs, nbJoueursIA, urlServeur, portServeur);
                new Client(nomJoueur, false).start();
                for (int i = 0; i < nbJoueursIA; i++) {
                    Thread.sleep(1000);
                    new Client("IA" + i, true).start();
                }
                break;
            case "2": // cas rejoindre une partie
                System.out.println("Veuillez renseigner une adresse (entrer 1 ou 2) :\n" +
                        "1) Adresse personnalisée (non implémenté pour le moment)\n" +
                        "2) Adresse par défaut (http://127.0.0.1:60001)");
                switch (lireEntree()) {
                    case "1": // cas adresse personnalisée
                        /*
                         * non implémenté pour le moment
                         */
                        break;
                    case "2": // cas adresse par défaut
                        urlServeur = "http://127.0.0.1:60001";
                        new Client(nomJoueur, false).start();
                        break;
                }
                break;
        }
    }

    private static String lireEntree() {
        return sc.nextLine();
    }

    private static void genererClients(int nbClients) throws InterruptedException {
        clients.add(new Client("Samuel", true));
        clients.add(new Client("Filipe", true));
        clients.add(new Client("Hugo", true));

        for (Thread client : clients) {
            client.start();
            Thread.sleep(1000);
        }
    }
}
