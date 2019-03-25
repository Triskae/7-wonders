package client.IA;

import client.Client;

public class IA {

    Client c;
    String strat;

    public IA(Client c, String strategie) {
        strat = strategie;
        this.c = c;
        c.setIA(true);
    }

    //Stratégie nulle joue juste une carte au hasard
    public void jouerCarteAlea(){
        double rand = (Math.random() * (c.getMain().getCartes().size()));
        c.playCard(c.getMain().getCartes().get((int) rand));
        c.getMain().getCartes().remove((int) rand);
    }

    public void jouerCarteParCouleur(int type){
        int bestScore=-1;
        int score=-1;
        int indice=-1;

        for(int i=0;i<c.getMain().getCartes().size();i++){ //parcours la main
            if(c.getMain().getCartes().get(i).getType()==type){ // Verifie si c'est une carte de type passer en paramètre
                score=c.getMain().getCartes().get(i).getPoint();
                if(bestScore<score){ //verifie qu'il y ai aucune carte avec plus de points dans la main
                    bestScore=score;
                    indice=i;
                }
            }
        }

        if(bestScore!=-1){ //s'il y a eu une carte du type en parametre et avec le plus de points
            c.playCard(c.getMain().getCartes().get(indice));
        }else{ //joue une carte aléatoire sinon
            jouerCarteAlea();
        }
    }


    public void tour() {
        switch (strat){
            case "random":
                jouerCarteAlea();
            case "bleu" :
                jouerCarteParCouleur(1);
            case "rouge" :
                jouerCarteParCouleur(2);
        }

    }

}
