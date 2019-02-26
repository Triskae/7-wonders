package commun;

import commun.cartes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {

	private static ArrayList<Carte> carteEnJeu = new ArrayList();
	ArrayList<Carte> deck = new ArrayList();

    public Deck(int nbJoueur) throws Exception {

        //3*7 = 3 joueur et 7 cartes par joueur

        createCarteEnJeu(nbJoueur);

        for (int i = 0; i < nbJoueur * 7; i++) {

            int r = new Random().nextInt(carteEnJeu.size());
            //Plus tard il faudra également faire un remove de la carte pour ne pas la piocher 2x
            deck.add(carteEnJeu.get(r));
        }
    }

	public ArrayList<Carte> getDeck() {
		return deck;
	}

	public static void createCarteEnJeu(int nbJoueur) throws Exception {

		switch (nbJoueur) {
			case 3: {
			    // Bleu
				carteEnJeu.add(new Bains());
				carteEnJeu.add(new Autel());
				carteEnJeu.add(new Theatre());
				carteEnJeu.add(new PreteurSurGages());
                // Rouge
                carteEnJeu.add(new Caserne());
                carteEnJeu.add(new TourDeGarde());
                carteEnJeu.add(new Palissade());
				break;
			}
			default: {
				throw new Exception("Pas encore crée");
			}
		}
	}

    public ArrayList<Carte> genererMain() {
        int nbCartes = 7;
        ArrayList<Carte> mainJoueur = new ArrayList<>();
        Collections.shuffle(deck);
        for (int i = 0; i < nbCartes; i++) {
            if (deck.size() < 7) {
                i = 0;
                nbCartes = deck.size();
            }
            Carte carte = deck.get(i);
            mainJoueur.add(carte);
            deck.remove(carte);
        }
        return mainJoueur;
    }
}
