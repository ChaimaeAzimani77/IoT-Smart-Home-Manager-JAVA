package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class AppareilConnecte implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static int nextId = 1; // Le compteur statique

    private final int id;
    private final String nom;
    private boolean connecte;
    protected String etatCourant = "N/A";

    public String getEtatCourant() {
        return etatCourant;
    }
    
    protected List<String> historique = new ArrayList<>();

    // ---  Méthode pour mettre à jour le compteur manuellement ---
    public static void setNextId(int valeur) {
        nextId = valeur;
    }
    
    public AppareilConnecte(String nom) {
        this.id = nextId++;
        this.nom = nom;
        this.connecte = false;
    }

    public void archiverMesure(String valeurMesure) {
        if (this.historique == null) {
            this.historique = new ArrayList<>();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateHeure = LocalDateTime.now().format(dtf);
        
        String log = "[" + dateHeure + "] " + valeurMesure;
        this.historique.add(log);
    }

    public List<String> getHistorique() {
        if (this.historique == null) {
            this.historique = new ArrayList<>();
        }
        return historique;
    }
    
    @Override
    public String toString() {
        return getType() + " : " + getNom();
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public boolean isConnecte() { return connecte; }

    public String afficher() {
        return getType() + " | " + nom + " | " + (connecte ? "Connecté" : "Déconnecté");
    }

    
    public void connecter() throws AppareilDejaConnecteException {
        if (connecte) throw new AppareilDejaConnecteException("Appareil déjà connecté");
        this.connecte = true;
    }

    public void deconnecter() throws AppareilNonConnecteException {
        if (!connecte) throw new AppareilNonConnecteException("Appareil non connecté");
        this.connecte = false;
    }

    public abstract String getType();
    public abstract String mesurer() throws AppareilException;
}