package tp5;

import model.GestionIoT;
import model.AppareilConnecte; 
import controller.IoTController;
import controller.LectureJSON;
import controller.GestionPersistance; 
import view.DashboardIoT;

import javax.swing.SwingUtilities;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciation du Modèle et du Contrôleur
        GestionIoT gestion = new GestionIoT();
        IoTController controller = new IoTController(gestion);

        // 2. Logique de chargement des données 
        System.out.println("--- Démarrage de l'application ---");
        
        // On essaie de lire le fichier de sauvegarde binaire
        List<AppareilConnecte> sauvegarde = GestionPersistance.charger();

        if (sauvegarde != null && !sauvegarde.isEmpty()) {
            // CAS A : Une sauvegarde existe, on la charge
            System.out.println("Sauvegarde trouvée ! Restauration de l'état précédent...");
            gestion.getAppareils().addAll(sauvegarde);

            int maxId = 0;
            for (AppareilConnecte a : sauvegarde) {
                if (a.getId() > maxId) maxId = a.getId();
            }
            AppareilConnecte.setNextId(maxId + 1);
            
            gestion.ajouterLog("Système restauré depuis la sauvegarde.");

        } else {
            // CAS B : Pas de sauvegarde (premier lancement ou reset), on charge le JSON
            System.out.println("Aucune sauvegarde détectée. Chargement de la configuration initiale (JSON)...");
            
            // On utilise le contrôleur pour charger le JSON
            controller.chargerDonneesInitiales(LectureJSON.lireAppareils("assets/appareils.json"));
            
            gestion.ajouterLog("Démarrage initial (chargement JSON).");
        }

        // 3. Lancement de l'interface graphique 
        SwingUtilities.invokeLater(() -> {
            // On passe 'gestion' qui contient maintenant les données (soit JSON, soit Sauvegarde)
            DashboardIoT dashboard = new DashboardIoT(gestion);
            dashboard.setVisible(true);
        });
    }
}