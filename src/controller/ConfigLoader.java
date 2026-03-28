package controller;

// Import pour lire un fichier depuis le disque
import java.io.FileInputStream;
// Import pour gérer les erreurs d'entrée/sortie
import java.io.IOException;
// Import pour utiliser un fichier de configuration clé = valeur
import java.util.Properties;

public class ConfigLoader {
    // Objet Properties qui va contenir les paramètres du fichier config.properties
    private static Properties props = new Properties();

    static {
        // Tentative d'ouverture du fichier config.properties
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis); // ChargemenInterfaceAppareilt des propriétés depuis le fichier
        } catch (IOException e) {
            System.err.println("Info : Pas de fichier config.properties trouvé, utilisation des valeurs par défaut.");
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}