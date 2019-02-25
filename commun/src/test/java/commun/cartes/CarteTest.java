package commun.cartes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarteTest {

    // Test cartes Batiment (Bleu)

    @org.junit.Test
    public void testCreationBatiment(){
        Bains bain = new Bains();
        assertEquals(3, bain.getNbPoint());
    }

    @org.junit.Test
     public void testNom(){
        Autel autel = new Autel();
        assertNotEquals("bain", autel.getNom());
    }

    // Test cartes Militaire

    @org.junit.Test
    public void testCreationMilitaire(){
        Caserne caserne = new Caserne();
        assertEquals(1, caserne.getNbAttaque());
    }

    // Test vitesse (tout est crée en moins d'une seconde)

    @org.junit.Test(timeout =  1000)
    public void testVitesseCreation(){
        new Autel();
        new Bains();
        new Theatre();
        new PreteurSurGages();
        new Palissade();
        new Caserne();
        new TourDeGarde();
    }


}
