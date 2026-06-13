package cm.edu.bibliotheque.listener;

import cm.edu.bibliotheque.service.AuthService;
import cm.edu.bibliotheque.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class DataInitializerListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            new AuthService().ensureDefaultAdmin();
            LOGGER.info("Initialisation des donnees terminee.");
        } catch (Throwable ex) {
            LOGGER.warn("Initialisation ignoree. Verifiez la base MySQL et les scripts SQL.", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
    }
}
