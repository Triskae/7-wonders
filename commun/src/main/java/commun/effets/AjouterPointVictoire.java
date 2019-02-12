package commun.effets;

public class AjouterPointVictoire extends Effet {

	private int nbDePoint;

	public AjouterPointVictoire(String nom, int nbPoint){
		super(nom);
		nbDePoint = nbPoint;
	}

	public int getNbDePoint() {
		return nbDePoint;
	}

}
