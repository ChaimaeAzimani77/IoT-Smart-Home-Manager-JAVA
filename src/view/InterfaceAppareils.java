package view;

import controller.LectureJSON;
import model.AppareilConnecte;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class InterfaceAppareils extends JFrame {

    public InterfaceAppareils() {
        setTitle("Liste des Appareils");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] colonnes = {"Type", "Nom", "Actif"};
        DefaultTableModel model = new DefaultTableModel(colonnes, 0);
        JTable table = new JTable(model);

        try {
            List<AppareilConnecte> appareils = LectureJSON.lireAppareils("assets/appareils.json");
            if (appareils != null) {
                for (AppareilConnecte a : appareils) {
                    Object[] ligne = {
                        a.getType(),
                        a.getNom(),
                        a.isConnecte() ? "Oui" : "Non"
                    };
                    model.addRow(ligne);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun appareil trouvé !");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lecture JSON");
            e.printStackTrace();
        }

        add(new JScrollPane(table));
    }
}
