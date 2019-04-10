package commun.cartes;
import commun.Ressource;
import commun.Symbole;

public abstract class CarteScientifique extends Carte{


    private Symbole symbole;


    public CarteScientifique(String nom, Ressource cout, Symbole symbole) {
        super(nom,cout);
        this.symbole = symbole;
    }

    public Symbole getSymbole() {
        return symbole;
    }

    public int getPoint(){return 0;}

    public int getType(){
        return 4;
    }

    public Ressource getCout() {
        return super.getCout();
    }

    @Override
    public String toString() {
        return this.getNom() + " [Symbole = " + symbole + "] ";
    }

}
