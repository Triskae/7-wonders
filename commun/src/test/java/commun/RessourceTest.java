package commun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RessourceTest {

    //Test ajout ressource

    @Test
    void ajouterRessource() throws Exception {
        Ressource r = new Ressource();
        r.ajouterRessource("Bois",5);
        r.ajouterRessource("Minerai",4);
        r.ajouterRessource("Pierre",3);
        r.ajouterRessource("Bois",5);
        assertEquals(10, (int)r.getRessource("Bois"));
        assertEquals(4, (int)r.getRessource("Minerai"));
    }

    @Test
    public void testAjoutRessourceIncconue() throws Exception {
        Assertions.assertThrows(Exception.class, () -> new Ressource().ajouterRessource("RessourceInconnue",5));
    }

    @Test
    public void testAjoutRessourceNegative() throws Exception {
        Assertions.assertThrows(Exception.class, () -> new Ressource().ajouterRessource("Bois",-2));
    }

}