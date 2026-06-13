package cm.edu.bibliotheque.servlet.upload;

import cm.edu.bibliotheque.util.ServletUtil;
import cm.edu.bibliotheque.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

@WebServlet("/upload/photo")
@MultipartConfig
public class UploadPhotoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String filename = UploadUtil.saveImage(request.getPart("photo"), uploadDirectory());
        ServletUtil.flash(request, "success", "Photo uploadee: " + filename);
        ServletUtil.redirect(request, response, "/membres");
    }

    private Path uploadDirectory() {
        String realPath = getServletContext().getRealPath("/uploads/photos");
        return realPath == null ? Path.of(System.getProperty("java.io.tmpdir"), "bibliotheque-uploads") : Path.of(realPath);
    }
}
