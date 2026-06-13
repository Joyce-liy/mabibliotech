package cm.edu.bibliotheque.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Properties;

public final class AppConfig {
    private static final Properties PROPERTIES = loadProperties();

    private AppConfig() {
    }

    public static String get(String key, String fallback) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv(toEnvironmentKey(key));
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        return PROPERTIES.getProperty(key, fallback);
    }

    public static int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(get(key, String.valueOf(fallback)));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal fallback) {
        try {
            return new BigDecimal(get(key, fallback.toPlainString()));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible de charger db.properties", ex);
        }
        return properties;
    }

    private static String toEnvironmentKey(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
    }
}
