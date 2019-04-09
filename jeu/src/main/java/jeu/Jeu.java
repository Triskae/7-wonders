package jeu;

import client.Client;
import serveur.Serveur;

import java.util.Scanner;

public class Jeu {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        String nomJoueur;
        do {
            nomJoueur = lireEntree("Veuillez entrer votre nom :");
        } while (nomJoueur.equals(""));
        formulaireClient(nomJoueur);
    }

    private static void formulaireClient(String nomJoueur) throws Exception {
        String urlServeur;
        int portServeur;
        int nbJoueurs = 3;
        int nbJoueursIA = 2;
        urlServeur = "127.0.0.1";
        portServeur = 60001;
        creerPartie(nbJoueurs, nbJoueursIA, urlServeur, portServeur, nomJoueur);

        /*
        Code optionnel qui permet au joueur de sélectionner différents paramètres pour la partie qui va être créée
        (nombre de joueurs total, nombre de joueurs IA ainsi que url et port du serveur)
         */
//        switch (lireEntree(nomJoueur + ", voulez vous (entrer 1 ou 2) :\n" +
//                "1) Créer une partie\n" +
//                "2) Rejoindre une partie")) {
//            case "1": // cas création d'une partie
//                int nbJoueurs = Integer.parseInt(lireEntree("Veuillez entrer le nombre de joueur de la partie vous compris (entre 3 (maximum pour le moment) et 7) :"));
//                while (nbJoueurs < 3 || nbJoueurs > 7) {
//                    nbJoueurs = Integer.parseInt(lireEntree("Veuillez entrer le nombre de joueur de la partie vous compris (entre 3 (maximum pour le moment) et 7) :"));
//                }
//                int nbJoueursIA = Integer.parseInt(lireEntree("Veuillez entrer le nombre de joueurs non-humains (" + (nbJoueurs - 1) + " au maximum) :"));
//                if (nbJoueursIA > (nbJoueurs - 1)) {
//                    nbJoueursIA = Integer.parseInt(lireEntree("Veuillez entrer le nombre de joueurs non-humains (" + (nbJoueurs - 1) + " au maximum) :"));
//                }
//                String entreeURL = lireEntree("Veuillez entrer une adresse pour le serveur (sans le port, ne rien entrer pour l'adresse par défaut 127.0.0.1) :");
//                if (entreeURL.equals("")) urlServeur = "127.0.0.1";
//                else urlServeur = entreeURL;
//                String entreePort = lireEntree("Veuillez entrer un port pour le serveur (ne rien entrer pour le port par défaut 60001) :");
//                if (entreePort.equals("")) portServeur = 60001;
//                else portServeur = Integer.parseInt(entreePort);
//                creerPartie(nbJoueurs, nbJoueursIA, urlServeur, portServeur, nomJoueur);
//                break;
//            case "2": // cas rejoindre une partie
//                formulaireRejoindrePartie(urlServeur, nomJoueur);
//                break;
//            default:
//                formulaireClient(nomJoueur);
//        }
    }

    /**
     * Cette méthode est utilisée dans le morceau de code optionnel ci-dessus, elle permet de demander au joueur quel
     * serveur de jeu il veut rejoindre
     * @param urlServeur url du serveur
     * @param nomJoueur nom du joueur
     */
    private static void formulaireRejoindrePartie(String urlServeur, String nomJoueur) {
        switch (lireEntree("Veuillez renseigner une adresse (entrer 1 ou 2) :\n" +
                "1) Adresse personnalisée (non implémenté pour le moment)\n" +
                "2) Adresse par défaut (http://127.0.0.1:60001)")) {
            case "1": // cas adresse personnalisée
                /*
                 * non implémenté pour le moment
                 */
                break;
            case "2": // cas adresse par défaut
                urlServeur = "http://127.0.0.1:60001";
                new Client(nomJoueur, false).start();
                break;
            default:
                formulaireRejoindrePartie(urlServeur, nomJoueur);
        }
    }


    private static void creerPartie(int nbJoueurs, int nbJoueursIA, String urlServeur, int portServeur, String nomJoueur) throws Exception {
        System.out.println("Création d'une partie pour " + nbJoueurs + " joueurs dont " + nbJoueursIA + " joueurs IA");
        new Serveur(nbJoueurs, nbJoueursIA, urlServeur, portServeur);
        new Client(nomJoueur, false).start();
        for (int i = 0; i < nbJoueursIA; i++) {
            Thread.sleep(1000);
            new Client("IA" + i, true).start();
        }
    }

    private static String lireEntree(String message) {
        System.out.println(message);
        return sc.nextLine();
    }
}
