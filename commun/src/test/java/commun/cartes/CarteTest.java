package commun.cartes;

import commun.Ressource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarteTest {

    // Test cartes Batiment (Bleu)

    @org.junit.Test
    public void testCreationBatiment(){
        Bains bain = new Bains();
        assertEquals(3, bain.getPoint());
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
        assertEquals(1, caserne.getPoint());
    }

    // Test vitesse (tout est crée en moins d'une seconde)

    @org.junit.Test(timeout =  1000)
    public void testVitesseCreation() throws Exception {
        new Autel();
        new Bains();
        new Theatre();
        new PreteurSurGages();
        new Palissade();
        new Caserne();
        new TourDeGarde();
        new Chantier();
        new Cavite();
        new BassinArgileux();
        new Filon();
        new Friche();
        new Excavation();
        new FosseArgileuse();
        new ExploitationForestiere();
        new Gisement();
        new Mine();
        new MetierATisser();
        new Verrerie();
        new Presse();
    }

    //Test ressources
    @org.junit.Test
    public void testRessource() throws Exception{
        Carte friche = new Friche();
        Ressource ressource = friche.getCout();
        assertEquals(1, (int)ressource.getRessource("Gold") );

        Carte bain = new Bains();
        Ressource ressource1 = bain.getCout();
        assertEquals(1,(int)ressource1.getRessource("Pierre"));

        Carte palissade = new Palissade();
        Ressource ressource2 = palissade.getCout();
        assertEquals(1, (int)ressource2.getRessource("Bois"));

        Carte officine = new Officine();
        Ressource ressource3 = officine.getCout();
        assertEquals(1, (int)ressource3.getRessource("Tissu"));

    }


}
