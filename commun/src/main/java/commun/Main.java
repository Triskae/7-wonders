package commun;

import commun.cartes.Carte;

import java.util.ArrayList;

public class Main {

    private ArrayList<Carte> cartes;

    public ArrayList<Carte> getCartes() {
        return cartes;
    }

    public void setCartes(ArrayList<Carte> cartes) {
        this.cartes = cartes;
    }

    public Main(ArrayList<Carte> cartes) {
        this.cartes = cartes;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int i = 1;
        for (Carte c : cartes) {
            str.append(i).append(") ").append(c).append("\n");
            i++;
        }
        return str.toString();
    }
}
