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

        if(nbJoueur>=3){
            // Bleu
            carteEnJeu.add(new Bains());
            carteEnJeu.add(new Autel());
            carteEnJeu.add(new Theatre());
            // Rouge
            carteEnJeu.add(new Caserne());
            carteEnJeu.add(new TourDeGarde());
            carteEnJeu.add(new Palissade());
            // Marron
            carteEnJeu.add(new Chantier());
            carteEnJeu.add(new Cavite());
            carteEnJeu.add(new BassinArgileux());
            carteEnJeu.add(new Filon());
            carteEnJeu.add(new FosseArgileuse());
            carteEnJeu.add(new ExploitationForestiere());
            // Blanche
            carteEnJeu.add(new MetierATisser());
            carteEnJeu.add(new Verrerie());
            carteEnJeu.add(new Presse());
        }

        if(nbJoueur>=4) {
            // Bleu
            carteEnJeu.add(new PreteurSurGages());
            // Marron
            carteEnJeu.add(new Chantier());
            carteEnJeu.add(new Filon());
            carteEnJeu.add(new Excavation());
            // Rouge
            carteEnJeu.add(new TourDeGarde());
        }

        if(nbJoueur>=5) {
            // Marron
            carteEnJeu.add(new Cavite());
            carteEnJeu.add(new BassinArgileux());
            carteEnJeu.add(new Gisement());
            // Bleu
            carteEnJeu.add(new Autel());
            // Rouge
            carteEnJeu.add(new Caserne());
        }

        if(nbJoueur>=6) {
            // Marron
            carteEnJeu.add(new Friche());
            carteEnJeu.add(new Mine());
            // Blanche
            carteEnJeu.add(new MetierATisser());
            carteEnJeu.add(new Verrerie());
            carteEnJeu.add(new Presse());
            // Bleu
            carteEnJeu.add(new Theatre());
        }

        if(nbJoueur==7) {
            // Bleu
            carteEnJeu.add(new PreteurSurGages());
            carteEnJeu.add(new Bains());
            // Rouge
            carteEnJeu.add(new Palissade());
        }
        if(nbJoueur>7){
            throw new Exception("Trop de joueur");
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
