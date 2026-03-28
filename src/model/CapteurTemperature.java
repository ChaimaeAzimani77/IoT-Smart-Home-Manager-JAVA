package model;

public class CapteurTemperature extends AppareilConnecte {
    public CapteurTemperature(String nom) { super(nom); }

    @Override
    public String getType() { return "Capteur de Température"; }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) throw new AppareilNonConnecteException("Capteur non connecté");
        double valeur = 15 + Math.random() * 16;
        etatCourant = valeur + " °C";
        return etatCourant;
    }
}
