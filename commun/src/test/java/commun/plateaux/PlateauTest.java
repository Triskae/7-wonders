package commun.plateaux;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlateauTest {

    // Test création plateau Gizeh
    @org.junit.Test
    public void testEtapesGizeh(){
        ArrayList<String> etapes = new ArrayList<String>();
        etapes.add("3 points");
        etapes.add("5 points");
        etapes.add("7 points");

        LaGrandePyramideDeGizeh gizeh = new LaGrandePyramideDeGizeh();
        assertEquals(etapes, gizeh.getEtapes());
    }

    @org.junit.Test
    public void testNomGizeh(){
        LaGrandePyramideDeGizeh gizeh = new LaGrandePyramideDeGizeh();
        assertEquals(RessourceDepart.PIERRE, gizeh.getRessource());
    }

    // Test création plateau Rhodes
    @org.junit.Test
    public void testEtapesRhodes(){
        ArrayList<String> etapes = new ArrayList<String>();
        etapes.add("3 points");
        etapes.add("2 guerres");
        etapes.add("7 points");

        LeColosseDeRhodes rhodes = new LeColosseDeRhodes();
        assertEquals(etapes, rhodes.getEtapes());
    }

    @org.junit.Test
    public void testNomRhodes(){
        LeColosseDeRhodes rhodes = new LeColosseDeRhodes();
        assertEquals(RessourceDepart.MINERAI, rhodes.getRessource());
    }

}
