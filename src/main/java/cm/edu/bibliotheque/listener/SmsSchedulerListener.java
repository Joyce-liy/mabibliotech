package cm.edu.bibliotheque.listener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cm.edu.bibliotheque.service.SmsService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SmsSchedulerListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSchedulerListener.class);
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        SmsService smsService = new SmsService();
        // planifier pour exécution quotidienne à 08:00
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(8).withMinute(0).withSecond(0).withNano(0);
        if (now.compareTo(nextRun) >= 0) {
            nextRun = nextRun.plusDays(1);
        }
        long initialDelay = Duration.between(now, nextRun).getSeconds();
        long period = TimeUnit.DAYS.toSeconds(1);
        future = scheduler.scheduleAtFixedRate(() -> {
            try {
                int sent = smsService.envoyerTousLesRappelsCount();
                LOGGER.info("Tâche planifiée: rappels J-2 envoyés: {}", sent);
            } catch (Throwable ex) {
                LOGGER.error("Erreur tâche rappels J-2", ex);
            }
        }, initialDelay, period, TimeUnit.SECONDS);
        LOGGER.info("Scheduler SMS J-2 initialisé, première exécution dans {} secondes", initialDelay);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (future != null) {
            future.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
