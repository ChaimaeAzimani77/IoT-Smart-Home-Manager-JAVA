package model;

public class ActionneurLumiere extends AppareilConnecte {
    
    private boolean on = false;

    public ActionneurLumiere(String nom) {
        super(nom);
    }

    @Override
    public String getType() {
        return "Actionneur Lumière";
    }

    // --- Actions (Boutons ON / OFF) ---
    public void allumer() {
        if (isConnecte()) {
            this.on = true;
            this.etatCourant = "ON"; 
            this.historique.add("Lumière allumée (ON)");
            System.out.println(getNom() + " : Allumé !");
        }
    }

    public void eteindre() {
        if (isConnecte()) {
            this.on = false;
            this.etatCourant = "OFF"; 
            this.historique.add("Lumière éteinte (OFF)");
            System.out.println(getNom() + " : Éteint !");
        }
    }
    
    @Override
    public String getEtatCourant() {
        return on ? "ON" : "OFF";
    }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) throw new AppareilNonConnecteException("Lumière non connectée");
        return getEtatCourant();    
    }
}