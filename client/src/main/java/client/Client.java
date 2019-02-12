package client;

import client.reseau.Connexion;
import commun.Identification;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {

    Identification moi = new Identification("Michel B", 42);
    Connexion connexion;
    int propositionCourante = 50;

    // Objet de synchro
    final Object attenteDéconnexion = new Object();

    public Client() {
    }


    /**
     * un ensemble de getter et setter
     **/


    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    private Connexion getConnexion() {
        return connexion;
    }


    public Identification getIdentification() {
        return moi;
    }


    private void seConnecter() {
        // on se connecte
        this.connexion.seConnecter();
        synchronized (attenteDéconnexion) {
            try {
                attenteDéconnexion.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void onConnexion() {
        System.out.println("connecté");
        connexion.emit("Coucou");
    }

    public void finPartie() {
        synchronized (attenteDéconnexion) {
//            attenteDéconnexion.notify();
        }
    }


    public void premierCoup() {
        //il envoie un truc au serveur
        // au premier coup, on envoie le nombre initial
//        connexion.envoyerCoup(propositionCourante);
    }


    public static final void main(String[] args) throws UnknownHostException {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        InetAddress inetAddress = InetAddress.getLocalHost();
        int port = 60001;
        String ipAdress = inetAddress.getHostAddress();

        Client client = new Client();
        Connexion connexion = new Connexion("http://"+ ipAdress + ":" + port, client);
        client.seConnecter();


        System.out.println("fin du main pour le client");

    }
}
