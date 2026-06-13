package cm.edu.bibliotheque.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class UploadUtil {
    private static final long MAX_SIZE = 2L * 1024L * 1024L;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    private UploadUtil() {
    }

    public static String saveImage(Part part, Path directory) throws IOException, ServletException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_SIZE) {
            throw new ServletException("Le fichier depasse 2 Mo.");
        }
        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ServletException("Formats acceptes: JPG ou PNG.");
        }
        Files.createDirectories(directory);
        String extension = "image/png".equalsIgnoreCase(contentType) ? ".png" : ".jpg";
        String filename = UUID.randomUUID() + extension;
        part.write(directory.resolve(filename).toString());
        return filename;
    }
}
