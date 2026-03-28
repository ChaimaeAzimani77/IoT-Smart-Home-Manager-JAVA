package model;

public class CapteurQualiteAir extends AppareilConnecte {

    public CapteurQualiteAir(String nom) {
        super(nom);
    }

    @Override
    public String getType() {
        return "Capteur Qualité Air";
    }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) {
            throw new AppareilNonConnecteException("Impossible de mesurer : capteur éteint");
        }
        int valeur = (int) (Math.random() * 100);
        etatCourant = valeur + " °C";
        return etatCourant;    }
}