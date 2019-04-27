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
    private static final String ANSI_GREEN = "\u001B[32m";

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
                    try {
                        client.addRessourceDepart(client.getPlateaux());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    connexion.emit("confirmationReceptionPlateau", new JSONArray().put(client.getNom()).put(client.getAJoue()));
                }
            });

            connexion.on("envoyerMain", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    ArrayList<Carte> mainRecue = new ArrayList<>();
                    JSONArray typesCartes = (JSONArray) objects[0];

                    for (int i = 0; i < typesCartes.length(); i++) {
                        try {
                            mainRecue.add((Carte) Class.forName(typesCartes.getString(i)).newInstance());
                        } catch (InstantiationException | IllegalAccessException | JSONException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    Main main = new Main(mainRecue);
                    client.setMain(main);
                    System.out.println("[CLIENT " + client.getNom() + "] - Réception de la main suivante\n" + client.getMain());
                    connexion.emit("confirmationReceptionMain");
                }
            });

            connexion.on("nouveauTour", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    System.out.println("[CLIENT " + client.getNom() + "] - J'ai reçu le message nouveau tour");
                    try {
                        client.tour(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        connexion.on("confirmationCarteDefaussee", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                client.setNombrePiece(client.getNombrePiece() + 3);
                if (!client.isIA()) System.out.println(ANSI_YELLOW + "[CLIENT " + client.getNom() + "] - Vous avez défaussé une carte et avez obtenu 3 pièces (nombre total de pièces : " + client.getNombrePiece() + ")" + ANSI_RESET);
            }
        });
    }

    public void jouer(JSONArray payload) throws JSONException {
        if ((int) payload.get(1) == 1) {
            System.out.println("[CLIENT " + client.getNom() + "] - J'essaie de défausser la carte d'indice " + payload.get(0));
        } else {
            System.out.println("[CLIENT " + client.getNom() + "] - J'essaie de jouer la carte d'indice " + payload.get(0));
        }
        connexion.emit("actionDeJeu", payload);
    }

    public void seConnecter() {
        connexion.connect();
    }
    public void emit(String str, Object... payload) {
        connexion.emit(str, payload);
    }
}
