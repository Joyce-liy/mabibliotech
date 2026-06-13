package cm.edu.bibliotheque.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        boolean loggedIn = httpRequest.getSession(false) != null
                && httpRequest.getSession(false).getAttribute("utilisateur") != null;

        if (loggedIn || isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(contextPath + "/login");
    }

    private boolean isPublic(String path) {
        return path.equals("/login")
                || path.equals("/index.jsp")
                || path.startsWith("/assets/")
                || path.startsWith("/uploads/")
                || path.startsWith("/WEB-INF/views/error/");
    }
}
