package commun;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;

    class DeckTest {

        @Test
        public void testCreationDeckPourTroisJoueur() throws Exception {
            Deck d = new Deck(3);
            assertEquals(21, d.getDeck().size());
        }


        //Test avec 5 joueurs

        @Test()
        public void testCreationDeckPourXJoueur() throws Exception {
            Assertions.assertThrows(Exception.class, () -> new Deck(5));

        }


    }
