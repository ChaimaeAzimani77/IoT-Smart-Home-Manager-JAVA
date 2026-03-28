package controller;

import model.AppareilConnecte;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {

    public static void exporterAppareils(List<AppareilConnecte> appareils, String cheminFichier) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Appareils IoT");

            // --- CRÉATION DE L'EN-TÊTE ---
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Type");
            header.createCell(2).setCellValue("Nom");
            header.createCell(3).setCellValue("Connecté");
            header.createCell(4).setCellValue("État");

            // --- REMPLISSAGE DES DONNÉES ---
            int rowNum = 1;
            for (AppareilConnecte a : appareils) {
                Row row = sheet.createRow(rowNum++);
                
                // On remplit les cellules correspondant aux colonnes
                row.createCell(0).setCellValue(a.getId());          // ID
                row.createCell(1).setCellValue(a.getType());        // Type
                row.createCell(2).setCellValue(a.getNom());         // Nom
                row.createCell(3).setCellValue(a.isConnecte() ? "Oui" : "Non"); // Connecté
                
                // Appel de la méthode polymorphe pour avoir l'état réel (ex: "22.5 °C" ou "ON")
                row.createCell(4).setCellValue(a.getEtatCourant()); 
            }

            // --- MISE EN FORME ---
            // On ajuste automatiquement la largeur des 5 colonnes (0 à 4)
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // --- SAUVEGARDE DU FICHIER ---
            FileOutputStream fos = new FileOutputStream(cheminFichier);
            workbook.write(fos);
            fos.close();

            System.out.println("Fichier Excel complet créé : " + cheminFichier);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'export Excel : " + e.getMessage());
        }
    }
}