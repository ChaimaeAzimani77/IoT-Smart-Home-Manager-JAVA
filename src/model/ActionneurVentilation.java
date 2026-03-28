package model;

public class ActionneurVentilation extends AppareilConnecte {
    
    // 0 = OFF, 1 = Faible, 2 = Moyen, 3 = Fort
    private int niveau = 0; 

    public ActionneurVentilation(String nom) {
        super(nom); 
    }

    @Override
    public String getType() {
        return "Actionneur Ventilation";
    }

    // --- Actions (Boutons + / -) ---
    public void augmenterVitesse() {
        // On vérifie la connexion 
        if (isConnecte() && niveau < 3) {
            niveau++;
            // POUR L'HISTORIQUE 
            this.historique.add("Vitesse augmentée au niveau " + niveau);
        }
    }

    public void diminuerVitesse() {
        // On vérifie la connexion 
        if (isConnecte() && niveau > 0) {
            niveau--;
            // POUR L'HISTORIQUE 
            this.historique.add("Vitesse diminuée au niveau " + niveau);
        } else if (isConnecte() && niveau == 0) {
             this.historique.add("Arrêt : Ventilation déjà à 0");
        }
    }

    // --- Affichage pour le Tableau ---
    @Override
    public String getEtatCourant() {
        if (niveau == 0) return "OFF (Arrêt)";
        return "Vitesse " + niveau;
    }

    // --- Mesure ---
    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) {
            throw new AppareilNonConnecteException("Ventilation non connectée");
        }
        return "Ventilateur tourne à : " + getEtatCourant();
    }
}