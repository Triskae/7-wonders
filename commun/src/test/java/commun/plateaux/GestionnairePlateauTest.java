package commun.plateaux;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GestionnairePlateauTest {

    // Test gestionnaire
    @org.junit.Test
    public void testRemplirGestionnaire(){
        GestionnairePlateau.RemplirPlateau();
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

        GestionnairePlateau.RemplirPlateau();
        Plateau p = GestionnairePlateau.RandomPlateau();

        assertTrue(liste.stream().anyMatch(item -> p.getClass().getSimpleName().equals(item)));
    }

}
