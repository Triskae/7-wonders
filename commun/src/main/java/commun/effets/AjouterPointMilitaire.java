package commun.effets;

public class AjouterPointMilitaire extends Effet {

        private int nbDePoint;

        public AjouterPointMilitaire(String nom, int nbPoint){
            super(nom);
            nbDePoint = nbPoint;
        }

        public int getNbDePoint() {
            return nbDePoint;
        }

}
