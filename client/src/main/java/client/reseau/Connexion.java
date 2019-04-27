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

            //Fonction appelé à chaque débu de tour pour recevoir la nouvelle main
            connexion.on("envoiMain", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {

                    // Reflexion et transformation de la liste de nom de carte en liste d'objet carte

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

                    if (client.getPlateaux() != null && client.getMain() != null) client.readyToPlay();
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
//                    if (client.isIA()) System.out.println(ANSI_PURPLE + "[IA " + client.getNom() + "] - Plateau reçu (" + client.getPlateaux() + ")" + ANSI_RESET);
//                    else System.out.println(ANSI_YELLOW + "[CLIENT " + client.getNom() + "] - Plateau reçu (" + client.getPlateaux() + ")" + ANSI_RESET);
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

            connexion.on("turn", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    try {
                        JSONArray numeros = (JSONArray) objects[0];
                        if (!client.isIA()) System.out.println(ANSI_GREEN + "=============== DEBUT DU TOUR " + numeros.getString(1) + " DE L'AGE " + numeros.getString(0) + " ===============" + ANSI_RESET);
                        client.setNumTour(Integer.parseInt(numeros.getString(1)));
                        client.setNumAge(Integer.parseInt(numeros.getString(0)));
                        client.tour(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            connexion.on("finTour", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    client.setAJoue(false);
                    System.out.println("------------- Passage dans le fintour -------------");
                    try {
                        JSONArray numeros = (JSONArray) objects[0];
                        if (!client.isIA()) System.out.println(ANSI_GREEN + "=============== DEBUT DU TOUR " + numeros.getString(1) + " DE L'AGE " + numeros.getString(0) + " ===============" + ANSI_RESET);
                        client.tour(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

            connexion.on("demanderPointsMilitaire", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    connexion.emit("envoyerPointsMilitaire", client.getNbBoucliers());
                }
            });

            connexion.on("confirmationCarteDefaussee", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    client.setNombrePiece(client.getNombrePiece() + 3);
                    if (!client.isIA()) System.out.println(ANSI_YELLOW + "[CLIENT " + client.getNom() + "] - Vous avez défaussé une carte et avez obtenu 3 pièces (nombre total de pièces : " + client.getNombrePiece() + ")" + ANSI_RESET);
                }
            });

            connexion.on("ajouterPointsVictoire", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    client.ajouterPointsVictoire((int) args[0]);
                    System.out.println("Passage dans points victoire" + " " + (int) args[0]);
                }
            });
    }

    public void jouer(JSONArray payload) throws JSONException {
        if ((int) payload.get(1) == 1) {
            System.out.println("[CLIENT " + client.getNom() + "] - J'essaye de défausser la carte d'indice " + payload.get(0));
        } else {
            System.out.println("[CLIENT " + client.getNom() + "] - J'essaye de jouer la carte d'indice " + payload.get(0));
        }
        connexion.emit("actionDeJeu", payload);
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
