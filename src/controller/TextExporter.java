package controller;

import model.AppareilConnecte;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TextExporter {

    public static void exporterAppareils(List<AppareilConnecte> appareils, String chemin) {
        try (FileWriter writer = new FileWriter(chemin)) {
            
            // On écrit d'abord les titres des colonnes pour que ce soit clair
            writer.write(String.format("%-5s | %-25s | %-20s | %-10s | %-20s%n", "ID", "Type", "Nom", "Connecté", "État"));
            writer.write("--------------------------------------------------------------------------------------------\n");

            for (AppareilConnecte a : appareils) {
                
                // On convertit le booléen en "Oui" ou "Non"
                String estConnecte = a.isConnecte() ? "Oui" : "Non";
                
                // On récupère l'état (ex: 22°C, ON, OFF...)
                String etat = a.getEtatCourant(); 

                String ligne = String.format("%-5s | %-25s | %-20s | %-10s | %-20s%n", 
                    a.getId(),      // L'ID
                    a.getType(),    // Le Type
                    a.getNom(),     // Le Nom
                    estConnecte,    // Connecté ?
                    etat            // L'État courant
                );

                writer.write(ligne);
            }
            
            System.out.println("Fichier TXT créé : " + chemin);
            
        } catch (IOException e) {
            System.out.println("Erreur export TXT : " + e.getMessage());
        }
    }
}