package cm.edu.bibliotheque.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BcryptUtilTest {
    private static final String ADMIN_HASH =
            "$2a$12$LDzcWbeGYrgPFhNlQ1VGWeu1XhTv29E62A8pC6/YiBEZKHfd8cw5m";

    @Test
    void verifieMotDePasseBCrypt() {
        assertTrue(BcryptUtil.matches("admin123", ADMIN_HASH));
        assertFalse(BcryptUtil.matches("admin", ADMIN_HASH));
    }
}
