package commun.effets;

public abstract class Effet {

	private String nom;

	public Effet(String nom) {
		this.nom=nom;
	}

	public String getNomEffet() {
		return nom;
	}

	public abstract int getNbDePoint();
}
