package client;

import client.reseau.Connexion;
import commun.cartes.Carte;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Thread {

    private Connexion connexion;
    private String nom;
    private ArrayList<Carte> main;
    private boolean connexionReussie;

    // Objet de synchro
    private final Object attenteDeconnexion = new Object();

    private Client(String nom, ArrayList<Carte> deckJoueur) {
        this.nom = nom;
        this.main = deckJoueur;
        this.connexionReussie = false;
    }

    public Client(String nom) {
        this.nom = nom;
        this.main = new ArrayList<Carte>();
        this.connexionReussie = false;
    }

    /**
     * un ensemble de getter et setter
     **/
    private String getNom() {
        return nom;
    }

    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    public ArrayList<Carte> getMain() {
        return main;
    }

    public void setMain(ArrayList<Carte> main) {
        this.main = main;
    }

    private Connexion getConnexion() {
        return connexion;
    }

    public boolean isConnexionReussie() {
        return connexionReussie;
    }

    private void seConnecter() {
        // on se connecte
        this.connexion.seConnecter();
        synchronized (attenteDeconnexion) {
            try {
                attenteDeconnexion.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onConnexion() {
        System.out.println("[CLIENT " + getNom() + "] - Connexion réussie");
        this.connexionReussie = true;
    }

    public void finPartie() {
        synchronized (attenteDeconnexion) {
            attenteDeconnexion.notify();
        }
        this.connexionReussie = false;
    }

    @Override
    public void run() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("[CLIENT " + getNom() + "] - Initialisation");

        int port = 60001;

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ipAdress = null;
        if (inetAddress != null) {
            ipAdress = inetAddress.getHostAddress();
        }

        Client c = new Client("client", this.main);
        connexion = new Connexion("http://"+ ipAdress + ":" + port, c);
        c.seConnecter();

        System.out.println("[CLIENT " + getNom() + "] - Déconnexion");
    }
}
