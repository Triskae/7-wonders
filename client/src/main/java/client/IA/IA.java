package client.IA;

import client.Client;
import commun.cartes.Carte;

import java.util.ArrayList;
import java.util.Random;

public class IA {

    private Client c;
    private String strat;
    private ArrayList<Carte> listeCartesInjouables = new ArrayList<>();
    private ArrayList<Carte> choixRestants = new ArrayList<>();

    public IA(Client c, String strategie) {
        strat = strategie;
        this.c = c;
        c.setIA(true);
    }

    public ArrayList<Carte> getListeCartesInjouables() {
        return listeCartesInjouables;
    }

    public void reinitialiserListeCartesInjouables() {
        this.listeCartesInjouables.clear();
    }

    public void setChoixRestants(ArrayList<Carte> choixRestants) {
        this.choixRestants = choixRestants;
    }

    public ArrayList<Carte> getChoixRestants() {
        return choixRestants;
    }

    public void ajouterCarteInjouable(Carte carte) {
        this.listeCartesInjouables.add(carte);
    }

    public void supprimerChoix(Carte carte) {
        this.choixRestants.remove(carte);
    }

    // Stratégie nulle joue juste une carte au hasard
    private void jouerCarteAlea() throws Exception {
        if (!c.getAJoue()) {
            if (!c.getMain().getCartes().isEmpty()) {
                Carte carteJouee = randomCarte(c.getMain().getCartes());
                c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
                c.setAJoue(true);
            }
        }
    }

    private void jouerCarteParCouleur(int type) throws Exception {
        if (!c.getAJoue()) {
            int bestScore=-1;
            int score;
            int indice=-1;

            for(int i=0;i<c.getMain().getCartes().size();i++) { //parcours la main
                if(c.getMain().getCartes().get(i).getType()==type){ // Verifie si c'est une carte de type passer en paramètre
                    score=c.getMain().getCartes().get(i).getPoint();
                    if(bestScore<score){ //verifie qu'il y ai aucune carte avec plus de points dans la main
                        bestScore=score;
                        indice=i;
                    }
                }
            }

            if(bestScore!=-1) { //s'il y a eu une carte du type en parametre et avec le plus de points
                Carte carteJouee = c.getMain().getCartes().get(indice);
                c.playCard(carteJouee, c.getMain().getCartes().indexOf(carteJouee));
                c.setAJoue(true);
            } else { //joue une carte aléatoire sinon
                jouerCarteAlea();
            }
        }
    }

    public void tour() throws Exception {
        switch (strat) {
            case "random":
                jouerCarteAlea();
            case "bleu" :
                jouerCarteParCouleur(1);
            case "rouge" :
                jouerCarteParCouleur(2);
        }
    }

    private static Carte randomCarte(ArrayList<Carte> cartes) {
        Random rand = new Random();
        return cartes.get(rand.nextInt(cartes.size()));
    }
}
