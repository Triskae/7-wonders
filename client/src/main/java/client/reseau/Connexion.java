package client.reseau;

import client.Client;
import commun.Coup;
import commun.Identification;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Connexion {

    private final Client controleur;
    Socket connexion;

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

            connexion.on("coucou", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    System.out.println("depuis le serveur");
                    System.out.println((Coup) objects[0]);

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

    public void envoyerId(Identification moi) {
        // conversion automatique obj <-> json
        JSONObject pieceJointe = new JSONObject(moi);
        connexion.emit("identification", pieceJointe);
    }

    public void emit(String str, Object... payload) {
        connexion.emit(str, payload);
    }
}
