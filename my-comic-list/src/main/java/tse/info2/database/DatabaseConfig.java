package tse.info2.database;

import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            // Charger le fichier de configuration
            properties.load(DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier db-config.properties : " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
