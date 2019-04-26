package client.IA;

import client.Client;
import commun.cartes.Carte;

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
    private void jouerCarteAlea() {
        if (!c.getAJoue()) {
            if (!c.getMain().getCartes().isEmpty()) {
                Carte carteJouee = randomCarte(c.getMain().getCartes());
                c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
                c.setAJoue(true);
            }
        }
    }

    /**
     * Essaye de jouer la carte avec le nombre de points le plus elevé du type donné en paramètre
     * Par exemple si 2 cartes science sont dans le deck une avec 2 points et l'autre avec 3, joue la 3, sinon carte random
     * Sinon joue une carte aléatoire
     * @param type Le type de la carte (carte bleu = 1, carte rouge = 2)
     */

    private void jouerCarteParCouleur(int type) {
        int bestScore = -1;
        int score;
        int indice = -1;


        // Extrait la carte avec le plus gros score
        for (int i = 0; i < c.getMain().getCartes().size(); i++) {
            if (c.getMain().getCartes().get(i).getType() == type) {
                score = c.getMain().getCartes().get(i).getPoint();
                if (bestScore < score) {
                    bestScore = score;
                    indice = i;
                }
            }
        }

        // Si il y a une carte du type en paramètre
        if (bestScore != -1) {
            Carte carteJouee = c.getMain().getCartes().get(indice);
            c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
            c.setAJoue(true);
        } else { //joue une carte aléatoire sinon
            jouerCarteAlea();
        }
    }

    public void tour() {
        switch (strat) {
            case "random":
                jouerCarteAlea();
            case "bleu":
                jouerCarteParCouleur(1);
            case "rouge":
                jouerCarteParCouleur(2);
        }
    }

    private static Carte randomCarte(ArrayList<Carte> cartes) {
        Random rand = new Random();
        return cartes.get(rand.nextInt(cartes.size()));
    }
}
