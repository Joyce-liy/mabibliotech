package cm.edu.bibliotheque.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class JPAUtil {
    private static final String PERSISTENCE_UNIT = "bibliothequePU";
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = buildEntityManagerFactory();

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, String> overrides = new HashMap<>();
        Properties properties = new Properties();

        try (InputStream input = JPAUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
                copyIfPresent(properties, overrides, "db.driver", "jakarta.persistence.jdbc.driver");
                copyIfPresent(properties, overrides, "db.url", "jakarta.persistence.jdbc.url");
                copyIfPresent(properties, overrides, "db.user", "jakarta.persistence.jdbc.user");
                copyIfPresent(properties, overrides, "db.password", "jakarta.persistence.jdbc.password");
                copyIfPresent(properties, overrides, "hibernate.hbm2ddl.auto", "hibernate.hbm2ddl.auto");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible de charger db.properties", ex);
        }

        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, overrides);
    }

    private static void copyIfPresent(Properties source, Map<String, String> target, String sourceKey, String targetKey) {
        String value = source.getProperty(sourceKey);
        if (value != null) {
            target.put(targetKey, value);
        }
    }
}
