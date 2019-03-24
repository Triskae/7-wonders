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

    public void jouerCarteBleu(){

    }

    public void tour() {
        switch (strat){
            case "bleu" :
                jouerCarteBleu();
        }

    }

}
