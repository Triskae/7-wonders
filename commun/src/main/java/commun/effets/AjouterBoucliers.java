package commun.effets;

public class AjouterBoucliers extends Effet {

        private int nbDePoint;

        public AjouterBoucliers(String nom, int nbPoint){
            super(nom);
            nbDePoint = nbPoint;
        }

        public int getNbDePoint() {
            return nbDePoint;
        }

}
