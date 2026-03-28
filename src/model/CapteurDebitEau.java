package model;

public class CapteurDebitEau extends AppareilConnecte {

    public CapteurDebitEau(String nom) {
        super(nom);
    }

    @Override
    public String getType() {
        return "Capteur Débit Eau";
    }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) {
            throw new AppareilNonConnecteException("Impossible de mesurer : capteur éteint");
        }
        double valeur = 1 + Math.random() * 19;
        etatCourant = valeur + " L/min";
        return etatCourant;
    }
}