package view;

import model.GestionIoT;
import model.AppareilConnecte;

// --- Imports des modèles ---
import model.CameraIP;
import model.ActionneurLumiere;
import model.CapteurHumidite;
import model.CapteurTemperature;
import model.CapteurQualiteAir;
import model.CapteurDebitEau;
import model.ActionneurVolet;
import model.ActionneurVentilation;

// --- Imports pour l'export et la persistance ---
import controller.ExcelExporter;
import controller.TextExporter;
import controller.GestionPersistance;
import controller.ConfigLoader; 

// --- Imports Swing et AWT ---
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class DashboardIoT extends JFrame {

    private final GestionIoT gestion;

    // --- Composants graphiques ---
    private final JTextField tfNomAjout = new JTextField(15);
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{
        "Capteur de Température", 
        "Capteur d'Humidité", 
        "Capteur Qualité Air", 
        "Capteur Débit Eau",
        "Actionneur Lumière", 
        "Caméra IP", 
        "Actionneur Volet", 
        "Actionneur Ventilation"
    });
    
    // Onglet Actions
    private final JComboBox<AppareilConnecte> cbAppareilsAction = new JComboBox<>();
    private final JTextArea taResult = new JTextArea(8, 30);
    
    // Onglet Historique
    private final JComboBox<AppareilConnecte> cbAppareilsHistorique = new JComboBox<>();
    private final JTextArea taHistorique = new JTextArea(15, 50);

    // Onglet Logs Système
    private final JTextArea taLogsSysteme = new JTextArea(15, 50);

    // Tableaux et Status
    private final AppareilTableModel tableModel;
    private final JTable table;
    private final JLabel statusBar = new JLabel(" Prêt");

    public DashboardIoT(GestionIoT gestion) {
        super("Gestion Réseau IoT");
        this.gestion = gestion;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        // ---------------------------------------------------------
        // CONFIGURATION DE LA FENÊTRE
        // ---------------------------------------------------------
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Sauvegarde automatique à la fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    GestionPersistance.sauvegarder(gestion.getAppareils());
                    System.out.println("Données sauvegardées avec succès.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardIoT.this, 
                        "Erreur sauvegarde : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        });

        setSize(1200, 750); 
        setLocationRelativeTo(null);

        // Initialisation du modèle de table
        tableModel = new AppareilTableModel(gestion);
        table = new JTable(tableModel);
        
        // STYLE DU TABLEAU 
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setShowGrid(false); 
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Entête du tableau
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.DARK_GRAY);

        applyCustomRenderer();
        
        // Création des onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        tabs.addTab("Accueil", buildAccueil()); 
        tabs.addTab("Appareils", buildAppareils());
        tabs.addTab("Actions & Tests", buildActions());
        tabs.addTab("Historique Mesures", buildHistorique());
        tabs.addTab("Logs Système", buildLogsSysteme());

        // Rafraîchissement automatique des onglets
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 4) { // Logs
                refreshLogs();
            } else if (tabs.getSelectedIndex() == 3) { // Historique
                 refreshAppareilsCombo();
            } else if (tabs.getSelectedIndex() == 0) { // Accueil
                tabs.setComponentAt(0, buildAccueil());
            }
        });

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);

        refreshAppareilsCombo();
    }

    // --- ONGLET 1 : ACCUEIL ---
    private JPanel buildAccueil() {
        JPanel p = new JPanel(new GridLayout(2, 1, 10, 10)); 
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Configuration
        JPanel pConfig = new JPanel(new GridLayout(5, 1));
        pConfig.setBorder(BorderFactory.createTitledBorder("Paramètres Généraux"));
        
        String nomMaison = "Ma Maison Intelligente";
        String seuilTemp = "25";
        String seuilHum = "50";
        try {
             nomMaison = ConfigLoader.get("nomMaison", "Maison IoT");
             seuilTemp = ConfigLoader.get("seuilTemp", "25");
             seuilHum = ConfigLoader.get("seuilHumidite", "50");
        } catch (Exception e) { }
        
        JLabel lblTitre = new JLabel("Bienvenue dans : " + nomMaison, SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitre.setForeground(new Color(0, 102, 204));
        
        pConfig.add(lblTitre);
        pConfig.add(new JLabel("Seuil Température Alerte : " + seuilTemp + "°C", SwingConstants.CENTER));
        pConfig.add(new JLabel("Seuil Humidité Alerte : " + seuilHum + "%", SwingConstants.CENTER));
        
        JPanel pStats = new JPanel(new GridLayout(3, 1));
        pStats.setBorder(BorderFactory.createTitledBorder("État du Système"));
        
        int total = gestion.getAppareils().size();
        long connectes = gestion.getAppareils().stream().filter(AppareilConnecte::isConnecte).count();
        long actionneurs = gestion.getAppareils().stream().filter(a -> a.getType().contains("Actionneur") || a.getType().contains("Caméra")).count();
        long capteurs = total - actionneurs;

        JLabel l1 = new JLabel("Nombre total d'appareils : " + total, SwingConstants.CENTER);
        JLabel l2 = new JLabel("Appareils Connectés : " + connectes, SwingConstants.CENTER);
        JLabel l3 = new JLabel("Détail : " + capteurs + " Capteurs | " + actionneurs + " Actionneurs", SwingConstants.CENTER);
        
        l1.setFont(new Font("Arial", Font.BOLD, 14));
        l2.setForeground(new Color(0, 128, 0)); 
        l2.setFont(new Font("Arial", Font.BOLD, 14));

        pStats.add(l1);
        pStats.add(l2);
        pStats.add(l3);

        p.add(pConfig);
        p.add(pStats);
        
        return p;
    }

    // --- ONGLET 2 : APPAREILS ---
    private JPanel buildAppareils() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // --- BARRE D'OUTILS HAUTE ---
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topToolbar.setBackground(new Color(225, 230, 235)); 
        topToolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Champs de saisie
        JLabel lblNom = new JLabel("Nom :");
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Boutons "Flat" 
        JButton btnAdd = createStyledButton("Ajouter", new Color(0, 123, 255), Color.WHITE); 
        JButton btnRefresh = createStyledButton("Rafraîchir", new Color(240, 240, 240), Color.BLACK);
        JButton btnExcel = createStyledButton("Excel", new Color(240, 240, 240), Color.BLACK);
        JButton btnTxt = createStyledButton("TXT", new Color(240, 240, 240), Color.BLACK);
        JButton btnSupprimer = createStyledButton("Supprimer", new Color(220, 53, 69), Color.WHITE); 

        // Ajout à la barre
        topToolbar.add(lblNom);
        topToolbar.add(tfNomAjout);
        topToolbar.add(cbType);
        topToolbar.add(btnAdd);
        topToolbar.add(Box.createHorizontalStrut(10)); 
        topToolbar.add(btnRefresh);
        topToolbar.add(btnExcel);
        topToolbar.add(btnTxt);
        topToolbar.add(Box.createHorizontalStrut(10)); 
        topToolbar.add(btnSupprimer);

        // --- BARRE DE CONTRÔLE SECONDAIRE (Boutons d'action) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Commandes Rapides"));
        
        JButton btnAllumer = new JButton("ON");
        JButton btnEteindre = new JButton("OFF");
        JButton btnMonter = new JButton("Monter ▲");
        JButton btnDescendre = new JButton("Descendre ▼");
        JButton btnVentilPlus = new JButton("Ventil +");
        JButton btnVentilMoins = new JButton("Ventil -");
        
        controlPanel.add(new JLabel("Lumière:"));
        controlPanel.add(btnAllumer);
        controlPanel.add(btnEteindre);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(new JLabel("Volet:"));
        controlPanel.add(btnMonter);
        controlPanel.add(btnDescendre);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(new JLabel("Ventilation:"));
        controlPanel.add(btnVentilPlus);
        controlPanel.add(btnVentilMoins);

        // --- LISTENERS ---
        btnAdd.addActionListener(e -> onAjouter());
        btnRefresh.addActionListener(e -> refreshTable("Tableau rafraîchi"));
        btnExcel.addActionListener(e -> exportData("xlsx"));
        btnTxt.addActionListener(e -> exportData("txt"));
        
        // --- LOGIQUE BOUTONS DE COMMANDE ---
        btnAllumer.addActionListener(e -> actionOnSelected(a -> ((ActionneurLumiere)a).allumer(), "Lumière allumée", ActionneurLumiere.class));
        btnEteindre.addActionListener(e -> actionOnSelected(a -> ((ActionneurLumiere)a).eteindre(), "Lumière éteinte", ActionneurLumiere.class));
        btnMonter.addActionListener(e -> actionOnSelected(a -> ((ActionneurVolet)a).monter(), "Volet monté", ActionneurVolet.class));
        btnDescendre.addActionListener(e -> actionOnSelected(a -> ((ActionneurVolet)a).descendre(), "Volet descendu", ActionneurVolet.class));
        btnVentilPlus.addActionListener(e -> actionOnSelected(a -> ((ActionneurVentilation)a).augmenterVitesse(), "Vitesse +", ActionneurVentilation.class));
        btnVentilMoins.addActionListener(e -> actionOnSelected(a -> ((ActionneurVentilation)a).diminuerVitesse(), "Vitesse -", ActionneurVentilation.class));

        // Suppression
        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à supprimer.");
                return;
            }
            AppareilConnecte app = gestion.getAppareils().get(row);
            int rep = JOptionPane.showConfirmDialog(this, 
                "Supprimer " + app.getNom() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

            if (rep == JOptionPane.YES_OPTION) {
                gestion.retirer(app);
                gestion.ajouterLog("Suppression : Appareil '" + app.getNom() + "' retiré."); 
                refreshTable("Appareil supprimé");
                refreshAppareilsCombo(); 
            }
        });        

        // Assemblage
        mainPanel.add(topToolbar, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(5, 15, 5, 15)); 
        return btn;
    }

    private <T> void actionOnSelected(java.util.function.Consumer<T> action, String logMsg, Class<T> clazz) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            AppareilConnecte app = gestion.getAppareils().get(row);
            if (!app.isConnecte()) {
                JOptionPane.showMessageDialog(this, "Impossible : L'appareil n'est pas connecté !", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (clazz.isInstance(app)) {
                action.accept(clazz.cast(app));
                gestion.ajouterLog("Action : " + logMsg + " sur " + app.getNom());
                refreshTable(logMsg);
            } else {
                JOptionPane.showMessageDialog(this, "Type d'appareil incorrect pour cette action.");
            }
        }
    }

    // --- ONGLET 3 : ACTIONS (Connexion / Mesures) ---
    private JPanel buildActions() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel controls = new JPanel();

        controls.add(new JLabel("Appareil :"));
        controls.add(cbAppareilsAction); 

        JButton b1 = new JButton("Connecter");
        JButton b2 = new JButton("Déconnecter");
        JButton b3 = new JButton("Mesurer / État");

        controls.add(b1);
        controls.add(b2);
        controls.add(b3);

        taResult.setEditable(false);
        taResult.setBorder(BorderFactory.createTitledBorder("Résultat de l'action"));

        b1.addActionListener(e -> onAction("connecter"));
        b2.addActionListener(e -> onAction("deconnecter"));
        b3.addActionListener(e -> onAction("mesurer"));

        p.add(controls, BorderLayout.NORTH);
        p.add(new JScrollPane(taResult), BorderLayout.CENTER);
        return p;
    }

    // --- ONGLET 4 : HISTORIQUE ---
    private JPanel buildHistorique() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        
        top.add(new JLabel("Voir historique de :"));
        top.add(cbAppareilsHistorique);
        JButton btnVoir = new JButton("Afficher");
        top.add(btnVoir);

        taHistorique.setEditable(false);
        taHistorique.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(taHistorique);
        scroll.setBorder(BorderFactory.createTitledBorder("Relevés enregistrés"));

        btnVoir.addActionListener(e -> afficherHistoriqueSelectionne());
        cbAppareilsHistorique.addActionListener(e -> afficherHistoriqueSelectionne());

        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void afficherHistoriqueSelectionne() {
        AppareilConnecte sel = (AppareilConnecte) cbAppareilsHistorique.getSelectedItem();
        taHistorique.setText("");
        if (sel == null) return;
        
        List<String> hist = sel.getHistorique();
        if (hist == null || hist.isEmpty()) {
            taHistorique.append("Aucun historique pour " + sel.getNom() + "\n");
        } else {
            taHistorique.append("--- Historique de " + sel.getNom() + " ---\n\n");
            for (String ligne : hist) {
                taHistorique.append(ligne + "\n");
            }
        }
    }

    // --- ONGLET 5 : LOGS SYSTEME ---
    private JPanel buildLogsSysteme() {
        JPanel p = new JPanel(new BorderLayout());
        taLogsSysteme.setEditable(false);
        taLogsSysteme.setBackground(new Color(240, 240, 240));
        taLogsSysteme.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnRefreshLogs = new JButton("Actualiser les Logs");
        btnRefreshLogs.addActionListener(e -> refreshLogs());

        p.add(new JScrollPane(taLogsSysteme), BorderLayout.CENTER);
        p.add(btnRefreshLogs, BorderLayout.SOUTH);
        return p;
    }

    private void refreshLogs() {
        taLogsSysteme.setText("--- JOURNAL DES ÉVÉNEMENTS SYSTÈME ---\n\n");
        for (String log : gestion.getLogsSysteme()) {
            taLogsSysteme.append(log + "\n");
        }
    }

    // --- LOGIQUE MÉTIER ---

    private void onAjouter() {
        String nom = tfNomAjout.getText().trim();
        String type = (String) cbType.getSelectedItem();

        if (nom.isEmpty()) { setStatus("Erreur : Nom manquant"); return; }

        AppareilConnecte a = null;
        if ("Capteur de Température".equals(type)) a = new CapteurTemperature(nom);
        else if ("Actionneur Lumière".equals(type)) a = new ActionneurLumiere(nom);
        else if ("Caméra IP".equals(type)) a = new CameraIP(nom);
        else if ("Capteur d'Humidité".equals(type)) a = new CapteurHumidite(nom);
        else if ("Capteur Qualité Air".equals(type)) a = new CapteurQualiteAir(nom);
        else if ("Capteur Débit Eau".equals(type)) a = new CapteurDebitEau(nom);
        else if ("Actionneur Volet".equals(type)) a = new ActionneurVolet(nom);
        else if ("Actionneur Ventilation".equals(type)) a = new ActionneurVentilation(nom);
        
        if (a == null) return;

        try {
            gestion.ajouter(a);
            gestion.ajouterLog("Ajout de l'appareil : " + nom + " (" + type + ")");
            setStatus("Succès : " + type + " ajouté.");
            tfNomAjout.setText("");
            refreshAppareilsCombo();
            tableModel.fireTableDataChanged();
        } catch (Exception ex) {
            setStatus("Erreur : " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAction(String action) {
        AppareilConnecte selected = (AppareilConnecte) cbAppareilsAction.getSelectedItem();
        if (selected == null) { setStatus("Aucun appareil sélectionné"); return; }

        String res;
        try {
            switch(action) {
                case "connecter": 
                    res = gestion.connecterAppareilById(selected.getId());
                    if(res.contains("Succès")) gestion.ajouterLog("Connexion : " + selected.getNom());
                    break;
                case "deconnecter": 
                    res = gestion.deconnecterAppareilById(selected.getId());
                    if(res.contains("Succès")) gestion.ajouterLog("Déconnexion : " + selected.getNom());
                    break;
                case "mesurer": 
                    res = gestion.mesurerAppareilById(selected.getId()); 
                    break;
                default: res = ""; break;
            }
            taResult.append(res + "\n");
            setStatus("Action effectuée");
        } catch (Exception ex) {
            taResult.append("Erreur : " + ex.getMessage() + "\n");
            setStatus("Erreur : " + ex.getMessage());
        }
        refreshAppareilsCombo();
        tableModel.fireTableDataChanged(); 
    }

    private void refreshAppareilsCombo() {
        cbAppareilsAction.removeAllItems();
        cbAppareilsHistorique.removeAllItems(); 
        
        for (AppareilConnecte a : gestion.getAppareils()) {
            cbAppareilsAction.addItem(a);
            cbAppareilsHistorique.addItem(a);
        }
        tableModel.fireTableDataChanged();
    }

    private void exportData(String type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("AppareilsIoT." + type));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if(!path.endsWith("." + type)) path += "." + type;
            
            try {
                if ("xlsx".equals(type)) ExcelExporter.exporterAppareils(gestion.getAppareils(), path);
                else TextExporter.exporterAppareils(gestion.getAppareils(), path);
                
                gestion.ajouterLog("Export : Données exportées en " + type.toUpperCase());
                setStatus("Export " + type.toUpperCase() + " réussi : " + path);
            } catch (Exception ex) {
                setStatus("Erreur Export : " + ex.getMessage());
            }
        }
    }

    private void refreshTable(String msg) {
        tableModel.fireTableDataChanged();
        setStatus(msg);
    }

    private void setStatus(String msg) {
        statusBar.setText(" " + msg);
    }

    private void applyCustomRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Appel au composant parent pour garder les fonctionnalités de base (sélection, focus)
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String valStr = (value != null) ? value.toString() : "";

                // Logique de couleur du texte
                if (column == 3 || column == 4) { // Colonnes "Connecté" et "État"
                    if ("Oui".equalsIgnoreCase(valStr) || "ON".equalsIgnoreCase(valStr) || 
                        valStr.contains("Ouvert") || valStr.contains("Marche")) {
                        c.setForeground(new Color(0, 150, 0)); 
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if ("Non".equalsIgnoreCase(valStr) || "OFF".equalsIgnoreCase(valStr) || 
                               valStr.contains("Fermé") || valStr.contains("Arrêt")) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                         c.setForeground(Color.BLACK); 
                    }
                } else {
                    c.setForeground(Color.DARK_GRAY);
                }

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(245, 245, 245)); 
                    }
                } else {
                    // Garder la couleur de sélection par défaut du Look&Feel
                }
                
                // Ajout d'un petit padding à gauche
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
                
                return c;
            }
        };
        
        // Appliquer ce renderer à toutes les colonnes
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // --- TABLE MODEL ---
    static class AppareilTableModel extends AbstractTableModel {
        private final GestionIoT gestion;
        private final String[] cols = {"ID", "Type", "Nom", "Connecté", "État"};

        public AppareilTableModel(GestionIoT g) { this.gestion = g; }

        @Override public int getRowCount() { return gestion.getAppareils().size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }

        @Override
        public Object getValueAt(int row, int col) {
            List<AppareilConnecte> list = gestion.getAppareils();
            if(row >= list.size()) return ""; 
            AppareilConnecte a = list.get(row);
            switch(col) {
                case 0: return a.getId();
                case 1: return a.getType();
                case 2: return a.getNom();
                case 3: return a.isConnecte() ? "Oui" : "Non";
                case 4: return a.getEtatCourant(); 
                default: return "";
            }
        }
    }
}