package jeu;

import client.Client;
import serveur.Serveur;

import java.util.Scanner;

public class Jeu {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        String urlServeur;
        int portServeur;
        int nbJoueurs = 3;
        int nbJoueursIA = 3;
        urlServeur = "127.0.0.1";
        portServeur = 60001;
        creerPartie(nbJoueurs, nbJoueursIA, urlServeur, portServeur);
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
                new Client(nomJoueur, false, "bleu").start();
                break;
            default:
                formulaireRejoindrePartie(urlServeur, nomJoueur);
        }
    }


    private static void creerPartie(int nbJoueurs, int nbJoueursIA, String urlServeur, int portServeur) throws Exception {
        System.out.println("Création d'une partie pour " + nbJoueurs + " joueurs dont " + nbJoueursIA + " joueurs IA");
        new Serveur(nbJoueurs, nbJoueursIA, urlServeur, portServeur);

        // Cas dans lequel on demande le nom du joueur humain.
        if (nbJoueurs != nbJoueursIA) {
            for (int j = 0; j < nbJoueurs; j++) {
                String nomJoueur;
                do {
                    nomJoueur = lireEntree("Veuillez entrer votre nom :");
                    new Client(nomJoueur, false, null).start();
                } while (nomJoueur.equals(""));
            }
        }

        // Cas pour donner un nom  aux IAs
        for (int i = 0; i < nbJoueursIA; i++) {
            new Client("IA" + i, true, "bleu").start();
            Thread.sleep(1000);
        }
    }

    private static String lireEntree(String message) {
        System.out.println(message);
        return sc.nextLine();
    }
}
