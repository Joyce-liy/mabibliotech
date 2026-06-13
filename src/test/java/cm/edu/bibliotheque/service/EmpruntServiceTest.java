package cm.edu.bibliotheque.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cm.edu.bibliotheque.enums.TypeMembre;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmpruntServiceTest {
    private final EmpruntService service = new EmpruntService();

    @Test
    void calculeDateRetourSelonTypeMembre() {
        LocalDate date = LocalDate.of(2026, 5, 11);

        assertEquals(LocalDate.of(2026, 5, 25),
                service.calculerDateRetourPrevue(TypeMembre.ETUDIANT, date));
        assertEquals(LocalDate.of(2026, 6, 10),
                service.calculerDateRetourPrevue(TypeMembre.ENSEIGNANT, date));
        assertEquals(LocalDate.of(2026, 5, 18),
                service.calculerDateRetourPrevue(TypeMembre.EXTERNE, date));
    }
}
