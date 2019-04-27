package client.IA;

import client.Client;
import commun.cartes.Carte;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

public class IA {

    private Client c;
    private String strat;

    public IA(Client c, String strategie) {
        strat = strategie;
        this.c = c;
        c.setIA(true);
    }

    // Stratégie nulle joue juste une carte au hasard
    private void jouerCarteAlea() throws JSONException {
        if (!c.getMain().getCartes().isEmpty()) {
            Carte carteJouee = randomCarte(c.getMain().getCartes());
            System.out.println("[CLIENT " + c.getNom() + "] - J'ai décidé d'une action de jeu avec la main suivante");
            System.out.println(c.getMain());
            c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
            // c.setAJoue(true);
        }
    }

    /**
     * Essaye de jouer la carte avec le nombre de points le plus elevé du type donné en paramètre
     * Par exemple si 2 cartes science sont dans le deck une avec 2 points et l'autre avec 3, joue la 3, sinon carte random
     * Sinon joue une carte aléatoire
     * @param type Le type de la carte (carte bleu = 1, carte rouge = 2)
     */

    private void jouerCarteParCouleur(int type) throws JSONException {
        int bestScore = -1;
        int indice = 0;

        for (int i = 0; i < c.getMain().getCartes().size(); i++) {
            if (c.getMain().getCartes().get(i).getType() == type) {
                if (c.getMain().getCartes().get(i).getPoint() >= bestScore) {
                    bestScore = c.getMain().getCartes().get(i).getPoint();
                    indice = i;
                }
            }
        }

        // Si il y a une carte du type en paramètre
        if (bestScore != -1) {
            Carte carteJouee = c.getMain().getCartes().get(indice);
            System.out.println("[CLIENT " + c.getNom() + "] - J'ai décidé d'une action de jeu avec la main suivante");
            System.out.println(c.getMain());
            c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
        } else { //joue une carte aléatoire sinon
            jouerCarteAlea();
        }
    }

    public void tour() throws JSONException {
        switch (strat) {
            case "random":
                jouerCarteAlea();
                break;
            case "bleu":
                jouerCarteParCouleur(1);
                break;
            case "rouge":
                jouerCarteParCouleur(2);
                break;
        }
    }

    private static Carte randomCarte(ArrayList<Carte> cartes) {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(cartes.size());
        return cartes.get(index);
    }
}
