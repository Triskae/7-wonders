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
import java.util.Arrays;

public class Connexion {

    private final Client client;
    private Socket connexion;

    public Connexion(String urlServeur, Client ctrl) {
        this.client = ctrl;
        client.setConnexion(this);

        try {
            connexion = IO.socket(urlServeur);

            connexion.on("connect", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    //Lorsque on est connecté
                    client.onConnexion();
                }
            });

            connexion.on("disconnect", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    connexion.disconnect();
                    connexion.close();
                    client.finPartie();

                }
            });

            connexion.on("turn", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    try {
                        client.tour();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                    client.setMain(new Main(mainRecue));
                    client.readyToPlay();
                }
            });

            connexion.on("envoiPlateau", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    String typePlateau = (String) objects[0];
                    try {
                        Plateau p = (Plateau) Class.forName(typePlateau).newInstance();
                        client.setPlateaux(p);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[CLIENT " + client.getNom() + "] - Plateau reçu (" + client.getPlateaux() + ")");
                    try {
                        client.addRessourceDepart(client.getPlateaux());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            connexion.on("finTour", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    client.setAJoue(false);
                    try {
                        client.tour();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    public Socket getSocket() {
        return connexion;
    }

    public void emit(String str, Object... payload) {
        connexion.emit(str, payload);
    }
}
