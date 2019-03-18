package commun.joueur;

import client.Client;

public class IA {

    Client c;
    String strat;

    public IA(Client t) {
        this.c = t;
        t.setIA(true);
    }

    public void jouerCarteAlea(){
        double rand = (Math.random() * (c.getMain().getCartes().size()));
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
