package commun.plateaux;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GestionnairePlateauTest {

    private GestionnairePlateau g = new GestionnairePlateau();

    // Test gestionnaire
    @org.junit.Test
    public void testRemplirGestionnaire(){
        g.RemplirPlateau();
        assertNotEquals(null, GestionnairePlateau.listePlateauDisponible);
    }

    @org.junit.Test
    public void testChoisirPlateauGestionnaire(){
        ArrayList<String> liste = new ArrayList<String>();
        liste.add("LeColosseDeRhodes");
        liste.add("LePhareDAlexandrie");
        liste.add("LeTempleDArthemisAEphese");
        liste.add("LesJardinsSuspendusDeBabylone");
        liste.add("LaStatueDeZeusAOlympie");
        liste.add("LeMausoléeDHalicarnasse");
        liste.add("LaGrandePyramideDeGizeh");

        g.RemplirPlateau();

        Plateau p = g.RandomPlateau();

        assertTrue(liste.stream().anyMatch(item -> p.getClass().getSimpleName().equals(item)));
    }

}
