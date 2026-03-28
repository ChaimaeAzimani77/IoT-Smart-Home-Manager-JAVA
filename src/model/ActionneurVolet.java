package model;

public class ActionneurVolet extends AppareilConnecte {
    
    // true = monté (ouvert), false = descendu (fermé)
    private boolean estMonte = false;

    public ActionneurVolet(String nom) {
        super(nom);
    }

    @Override
    public String getType() {
        return "Actionneur Volet";
    }

    // --- Actions (Boutons Monter / Descendre) ---
    public void monter() { 
        if (isConnecte()) {
            this.estMonte = true; 
            // POUR L'HISTORIQUE :
            this.historique.add("Volet monté (Ouvert)");
        }
    }
    
    public void descendre() { 
        if (isConnecte()) {
            this.estMonte = false; 
            // POUR L'HISTORIQUE :
            this.historique.add("Volet descendu (Fermé)");
        }
    }

    // --- Affichage pour le Tableau ---
    @Override
    public String getEtatCourant() {
        return estMonte ? "Ouvert (Monté)" : "Fermé (Descendu)";
    }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) {
            throw new AppareilNonConnecteException("Volet non connecté");
        }
        return "Position actuelle : " + getEtatCourant();
    }
}