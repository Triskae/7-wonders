package commun.plateaux;

import java.util.ArrayList;

public class GestionnairePlateau {

    static ArrayList<Plateau> listePlateauDisponible = new ArrayList<Plateau>();

    public GestionnairePlateau() {
        RemplirPlateau();
    }

    public void RemplirPlateau() {
        listePlateauDisponible.clear();
        listePlateauDisponible.add(new LeColosseDeRhodes());
        listePlateauDisponible.add(new LePhareDAlexandrie());
        listePlateauDisponible.add(new LeTempleDArthemisAEphese());
        listePlateauDisponible.add(new LesJardinsSuspendusDeBabylone());
        listePlateauDisponible.add(new LaStatueDeZeusAOlympie());
        listePlateauDisponible.add(new LeMausoléeDHalicarnasse());
        listePlateauDisponible.add(new LaGrandePyramideDeGizeh());
    }

    public Plateau RandomPlateau() {
        if(listePlateauDisponible.size() != 0) {
            double val = Math.random() * ( listePlateauDisponible.size() - 0 );
            Plateau p = listePlateauDisponible.get((int)val);
            listePlateauDisponible.remove((int)val);
            return p;
        }
        return null;
    }

    public ArrayList<Plateau> getListePlateauDisponible() {
        return listePlateauDisponible;
    }
}
