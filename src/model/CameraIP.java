package model;
import java.io.Serializable;

public class CameraIP extends AppareilConnecte implements Serializable {
    private static final long serialVersionUID = 1L; 
    private boolean active = false; // true = activée, false = désactivée

    public CameraIP(String nom) { 
        super(nom); 
    }

    @Override
    public String getType() { 
        return "Caméra IP"; 
    }

    @Override
    public String mesurer() throws AppareilException {
        if (!isConnecte()) 
            throw new AppareilNonConnecteException("Caméra non connectée");
        
        // On inverse l'état actuel
        active = !active;
        etatCourant = active ? "ACTIVÉE" : "DÉSACTIVÉE";
        return etatCourant;    
    }
}
