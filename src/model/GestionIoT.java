package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestionIoT {
    
    private final List<AppareilConnecte> appareils = new ArrayList<>();
    
    // --- La liste pour stocker l'historique global (Logs Système) ---
    private final List<String> logsSysteme = new ArrayList<>();

    public GestionIoT() {
        ajouterLog("Système IoT démarré.");
    }

    public List<AppareilConnecte> getAppareils() { return appareils; }

    // --- Les méthodes pour gérer les logs  ---
    
    public void ajouterLog(String message) {
        // Pour ajouter l'heure actuelle
        String heure = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String log = "[" + heure + "] " + message;
        
        logsSysteme.add(log);
        System.out.println("LOG: " + log); 
    }

    public List<String> getLogsSysteme() {
        return logsSysteme;
    }

    public void ajouter(AppareilConnecte a) throws AppareilDejaExistantException {
        for (AppareilConnecte x : appareils) {
            if (x.getNom().equalsIgnoreCase(a.getNom()) && x.getType().equals(a.getType())) {
                throw new AppareilDejaExistantException("Cet appareil existe déjà : " + a.getNom() + " (" + a.getType() + ")");
            }
        }
        
        appareils.add(a);
        ajouterLog("Nouvel appareil ajouté : " + a.getNom());
    }

    public AppareilConnecte getAppareilById(int id) throws AppareilIntrouvableException {
        for (AppareilConnecte a : appareils) {
            if (a.getId() == id) {
                return a;
            }
        }
        throw new AppareilIntrouvableException("Appareil introuvable avec l'ID : " + id);
    }    

    public String connecterAppareilById(int id) throws AppareilException {
        for (AppareilConnecte a : appareils) {
            if (a.getId() == id) {
                if (a.isConnecte()) throw new AppareilDejaConnecteException("Déjà connecté");
                
                a.connecter();
                ajouterLog("Connexion établie : " + a.getNom());
                
                return a.getNom() + " connecté";
            }
        }
        throw new AppareilIntrouvableException("Appareil introuvable");
    }

    public String deconnecterAppareilById(int id) throws AppareilException {
        for (AppareilConnecte a : appareils) {
            if (a.getId() == id) {
                if (!a.isConnecte()) throw new AppareilNonConnecteException("Déjà déconnecté");
                
                a.deconnecter();
                ajouterLog("Déconnexion : " + a.getNom());
                
                return a.getNom() + " déconnecté";
            }
        }
        throw new AppareilIntrouvableException("Appareil introuvable");
    }

    public String mesurerAppareilById(int id) throws AppareilException {
        AppareilConnecte a = getAppareilById(id);
        
        String resultat = a.mesurer(); 
        
        a.archiverMesure(resultat);
        
        ajouterLog("Mesure (" + a.getNom() + ") : " + resultat);
        
        return a.getType() + " (" + a.getNom() + ") : " + resultat;
    }
    
    public void retirer(AppareilConnecte appareil) {
        if (appareils.remove(appareil)) {
            ajouterLog("Suppression de l'appareil : " + appareil.getNom());
            System.out.println("Appareil supprimé du système.");
        }
    }
}