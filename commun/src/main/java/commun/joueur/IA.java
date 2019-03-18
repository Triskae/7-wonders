package commun.joueur;

import client.Client;

public class IA {

    Client t;

    public IA(Client t) {
        this.t = t;
        t.setIA(true);
    }

    public void jouerCarteAlea(){
        double rand = (Math.random() * (t.getMain().getCartes().size()));
        t.getMain().getCartes().remove((int) rand);
    }

    public void jouerCarteBleu(){

    }

}
