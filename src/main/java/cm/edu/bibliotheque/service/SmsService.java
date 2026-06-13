package cm.edu.bibliotheque.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cm.edu.bibliotheque.dao.EmpruntDAO;
import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Penalite;

public class SmsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    private final boolean smsEnabled;
    private final String twilioSid;
    private final String twilioToken;
    private final String twilioFrom;

    public SmsService() {
        Properties props = new Properties();
        String sid = null, token = null, from = null;
        boolean enabled = false;
        try (InputStream in = SmsService.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                sid = props.getProperty("twilio.sid");
                token = props.getProperty("twilio.token");
                from = props.getProperty("twilio.from");
                String enabledProp = props.getProperty("twilio.enabled");
                enabled = "true".equalsIgnoreCase(enabledProp);
            }
        } catch (IOException ex) {
            LOGGER.warn("Impossible de lire db.properties pour Twilio", ex);
        }
        this.twilioSid = sid;
        this.twilioToken = token;
        this.twilioFrom = from;
        this.smsEnabled = enabled && sid != null && token != null && from != null;
        if (this.smsEnabled) {
            LOGGER.info("Twilio mode activé (envoi via API) ");
        } else {
            LOGGER.info("Mode SMS désactivé ou configuration Twilio manquante; les envois seront simulés.");
        }
    }

    public void envoyerRappelRetour(Emprunt emprunt) {
        if (!hasTelephone(emprunt)) {
            return;
        }
        String message = "Retournez le livre " + emprunt.getOuvrage().getTitre()
                + " avant le " + emprunt.getDateRetourPrevue()
                + " pour eviter une penalite";
        send(emprunt.getMembre().getTelephone(), message);
    }

    public void envoyerRelanceRetard(Emprunt emprunt, Penalite penalite) {
        if (!hasTelephone(emprunt)) {
            return;
        }
        long jours = ChronoUnit.DAYS.between(emprunt.getDateRetourPrevue(), LocalDate.now());
        String montant = penalite == null ? "a calculer" : penalite.getMontantFcfa() + " FCFA";
        String message = "Votre emprunt est en retard de " + jours + " jours. Penalite: " + montant;
        send(emprunt.getMembre().getTelephone(), message);
    }

    public void envoyerTousLesRappels() {
        List<Emprunt> list = empruntDAO.findRetourDansDeuxJours();
        list.forEach(this::envoyerRappelRetour);
    }

    public int envoyerTousLesRappelsCount() {
        List<Emprunt> list = empruntDAO.findRetourDansDeuxJours();
        list.forEach(this::envoyerRappelRetour);
        return list.size();
    }

    private boolean hasTelephone(Emprunt emprunt) {
        return emprunt != null
                && emprunt.getMembre() != null
                && emprunt.getMembre().getTelephone() != null
                && !emprunt.getMembre().getTelephone().isBlank();
    }

    private void send(String telephone, String message) {
        if (smsEnabled) {
            try {
                // Envoi via Twilio REST API sans SDK
                String sid = twilioSid;
                String token = twilioToken;
                String from = twilioFrom;
                String uri = "https://api.twilio.com/2010-04-01/Accounts/" + URLEncoder.encode(sid, StandardCharsets.UTF_8) + "/Messages.json";

                String body = "To=" + URLEncoder.encode(telephone, StandardCharsets.UTF_8)
                        + "&From=" + URLEncoder.encode(from, StandardCharsets.UTF_8)
                        + "&Body=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

                String auth = sid + ":" + token;
                String basicAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .header("Authorization", basicAuth)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();
                if (code >= 200 && code < 300) {
                    LOGGER.info("SMS envoyé via Twilio vers {} (status={})", telephone, code);
                } else {
                    LOGGER.error("Echec envoi SMS vers {} via Twilio (status={}, body={})", telephone, code, response.body());
                }
            } catch (Exception ex) {
                LOGGER.error("Erreur en envoyant le SMS vers {}: {}", telephone, ex.getMessage());
            }
        } else {
            LOGGER.info("SMS simule vers {}: {}", telephone, message);
        }
    }
}
