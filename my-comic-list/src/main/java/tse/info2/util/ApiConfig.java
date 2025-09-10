package tse.info2.util;

import java.io.IOException;
import java.util.Properties;

public class ApiConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ApiConfig.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de config.properties : " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
