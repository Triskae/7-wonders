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

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";

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
                    client.tour();
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
                    if (client.isIA()) {
                        client.getInstanceIA().setChoixRestants(mainRecue);
                        client.getInstanceIA().reinitialiserListeCartesInjouables();
                    }
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
                    if (client.isIA()) System.out.println(ANSI_PURPLE + "[CLIENT " + client.getNom() + "] - Plateau reçu (" + client.getPlateaux() + ")" + ANSI_RESET);
                    else System.out.println(ANSI_YELLOW + "[CLIENT " + client.getNom() + "] - Plateau reçu (" + client.getPlateaux() + ")" + ANSI_RESET);
                    try {
                        client.addRessourceDepart(client.getPlateaux());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            connexion.on("demanderPointsMilitaire", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    connexion.emit("envoyerPointsMilitaire", client.getPointMilitaire());
                }
            });

            connexion.on("confirmationCarteDefaussee", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    client.setNombrePiece(client.getNombrePiece() + 3);
                    System.out.println(ANSI_YELLOW + "[CLIENT " + client.getNom() + "] - Vous avez défaussé une carte et avez obtenu 3 pièces (nombre total de pièces : " + client.getNombrePiece() + ")" + ANSI_RESET);
                }
            });

            connexion.on("finTour", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    client.setAJoue(false);
                    client.tour();
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
