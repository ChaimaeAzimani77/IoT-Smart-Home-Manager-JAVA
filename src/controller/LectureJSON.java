package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class LectureJSON {
    public static List<AppareilJSON> lireAppareils(String cheminFichier) {
        try {
            Gson gson = new Gson();
            InputStream is = LectureJSON.class.getClassLoader().getResourceAsStream(cheminFichier);
            if (is == null) throw new RuntimeException("Fichier JSON introuvable : " + cheminFichier);

            InputStreamReader reader = new InputStreamReader(is);
            Type typeListe = new TypeToken<List<AppareilJSON>>(){}.getType();
            return gson.fromJson(reader, typeListe);
        } catch (RuntimeException e) { e.printStackTrace(); return null; }
    }

    public static class AppareilJSON { 
        public String type; 
        public String nom; 
        public boolean actif; 
    }
}