package model;

public class CapteurHumidite extends AppareilConnecte {
    public CapteurHumidite(String nom) { super(nom); }

    @Override
    public String getType() { return "Capteur d'Humidité"; }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) throw new AppareilNonConnecteException("Capteur non connecté");        
        double valeur = 30 + Math.random() * 60;
        etatCourant = valeur + " %";
        return etatCourant;       
    }
}
