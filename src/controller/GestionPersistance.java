package controller;

import model.AppareilConnecte;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestionPersistance {

    private static final String FICHIER_SAUVEGARDE = "sauvegarde_iot.dat";

    // Sauvegarder la liste des appareils
    public static void sauvegarder(List<AppareilConnecte> appareils) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_SAUVEGARDE))) {
            oos.writeObject(appareils);
            System.out.println("Sauvegarde effectuée dans " + FICHIER_SAUVEGARDE);
        }
    }

    // Charger la liste des appareils
    @SuppressWarnings("unchecked")
    public static List<AppareilConnecte> charger() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if (!fichier.exists()) {
            System.out.println("Aucune sauvegarde trouvée. Démarrage à vide.");
            return new ArrayList<>(); // Retourne une liste vide si pas de fichier
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHIER_SAUVEGARDE))) {
            return (List<AppareilConnecte>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement de la sauvegarde : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}