package commun.plateaux;

import java.util.ArrayList;

public enum RessourceDepart {

  ARGILE("Argile"),
  MINERAI ("Minerai"),
  PIERRE ("Pierre"),
  BOIS ("Bois"),
  VERRE ("Verre"),
  TISSU ("Tissu"),
  PAPYRUS ("Papyrus");

  private String name = "";

  RessourceDepart(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String toString(){
    return name;
  }

}
