package cm.edu.bibliotheque.util;

import jakarta.servlet.http.HttpServletRequest;

public final class PaginationUtil {
    private PaginationUtil() {
    }

    public static int page(HttpServletRequest request) {
        return positiveInt(request.getParameter("page"), 1);
    }

    public static int size(HttpServletRequest request) {
        return positiveInt(request.getParameter("size"), 10);
    }

    public static int totalPages(long total, int size) {
        if (total <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) total / size);
    }

    private static int positiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (RuntimeException ex) {
            return fallback;
        }
    }
}
