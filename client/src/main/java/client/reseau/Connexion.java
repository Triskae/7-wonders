package client.reseau;

import client.Client;
import commun.Main;
import commun.cartes.Carte;
import commun.plateaux.Plateau;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class Connexion {

    private final Client controleur;
    private Socket connexion;

    public Connexion(String urlServeur, Client ctrl) {
        this.controleur = ctrl;
        controleur.setConnexion(this);

        try {
            connexion = IO.socket(urlServeur);

            connexion.on("connect", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    //Lorsque on est connecté
                    controleur.onConnexion();
                    connexion.emit("envoiIdentification", controleur.getNom());
                }
            });

            connexion.on("disconnect", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    connexion.disconnect();
                    connexion.close();
                    controleur.finPartie();

                }
            });

            connexion.on("envoiMain", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    ArrayList<Carte> mainRecue = new ArrayList<>();
                    Object carteTemp;
                    JSONArray typesCartes = (JSONArray) objects[0];
                    for (int i = 0; i < typesCartes.length(); i++) {
                        try {
                            carteTemp = Class.forName(typesCartes.getString(i)).newInstance();
                            mainRecue.add((Carte) carteTemp);
                        } catch (InstantiationException | IllegalAccessException | JSONException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    controleur.setMain(new Main(mainRecue));
                    System.out.println("[CLIENT " + controleur.getNom() + "] - Main reçue");
                    System.out.println("[CLIENT " + controleur.getNom() + "] - " + controleur.getMain());
                }
            });

            connexion.on("envoiPlateau", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    String typePlateau = (String) objects[0];
                    try {
                        Plateau p = (Plateau) Class.forName(typePlateau).newInstance();
                        controleur.setPlateaux(p);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[CLIENT " + controleur.getNom() + "] - Plateau reçu (" + controleur.getPlateaux() + ")");
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void seConnecter() {
        // on se connecte
        connexion.connect();
    }

    public void emit(String str, Object... payload) {
        connexion.emit(str, payload);
    }
}
