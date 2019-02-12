package commun;

import commun.cartes.*;

import java.util.ArrayList;
import java.util.Random;

public class main {

	private static ArrayList<Carte> carteEnJeu = new ArrayList();

	public static void main(String[] args) {

		ArrayList<Carte> deck = new ArrayList();
		//3*7 = 3 joueur et 7 cartes par joueur

		createCarteEnJeu(3);

		for (int i=0 ; i<3*7; i++) {

			int r = new Random().nextInt(carteEnJeu.size());
			//Plus tard il faudra également faire un remove de la carte pour ne pas la piocher 2x
			deck.add(carteEnJeu.get(r));

		}

		for (int i=0 ; i<deck.size(); i++) {
			System.out.println(i + " " + deck.get(i));
		}

	}


	public static void createCarteEnJeu(int nbJoueur) {

		switch (nbJoueur) {
			case 3: {
				carteEnJeu.add(new Bains());
				carteEnJeu.add(new Autel());
				carteEnJeu.add(new Theatre());
				carteEnJeu.add(new PreteurSurGages());
				break;
			}
		}

	}


}
