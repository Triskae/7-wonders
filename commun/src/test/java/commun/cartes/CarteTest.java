package commun.cartes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarteTest {

    @Test
    public void getNom() {
    }

    @org.junit.Test
    public void testCreation(){
        Bains bain = new Bains();
        assertEquals(3, bain.getNbPoint());
    }

    @org.junit.Test
     public void test(){
        Autel autel = new Autel();
        assertNotEquals("bain", autel.getNom());
    }
}
