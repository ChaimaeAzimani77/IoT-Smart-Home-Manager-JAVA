package controller;

import model.*;
import java.util.List;

public class IoTController {
    private final GestionIoT model;

    public IoTController(GestionIoT model) { this.model = model; }

    public void chargerDonneesInitiales(List<LectureJSON.AppareilJSON> appareils) {
        if (appareils != null) {
            for (LectureJSON.AppareilJSON a : appareils) {
                try { model.ajouter(convertir(a)); } 
                catch (Exception ignored) {}
            }
        }
    }

    private AppareilConnecte convertir(LectureJSON.AppareilJSON a) throws Exception {
        switch (a.type) {
            case "CapteurTemperature": return new CapteurTemperature(a.nom);
            case "CapteurHumidite": return new CapteurHumidite(a.nom);
            case "ActionneurLumiere": return new ActionneurLumiere(a.nom);
            case "CameraIP": return new CameraIP(a.nom);
            case "CapteurQualiteAir": return new CapteurQualiteAir(a.nom);
            case "CapteurDebitEau": return new CapteurDebitEau(a.nom);
            case "ActionneurVolet": return new ActionneurVolet(a.nom);
            case "ActionneurVentilation": return new ActionneurVentilation(a.nom);            
            default: throw new Exception("Type inconnu : " + a.type);
        }
    }

    // Méthodes d'action directes 
    public String connecter(int id) throws AppareilException { return model.connecterAppareilById(id); }
    public String deconnecter(int id) throws AppareilException { return model.deconnecterAppareilById(id); }
    public String mesurer(int id) throws AppareilException { return model.mesurerAppareilById(id); }
}
