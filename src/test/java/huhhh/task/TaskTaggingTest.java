package huhhh.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import huhhh.HuhhhException;

public class TaskTaggingTest {

    @Test
    void addTag_normalizesAndDeduplicates() {
        Task t = new Todo("read book");
        try {
            t.addTag("#Fun");
            t.addTag("#fun");
        } catch (HuhhhException e) {
            // This should not happen in this test
            assert false : "Unexpected exception: " + e.getMessage();
        }

        assertTrue(t.hasTag("#FUN"));
        assertTrue(t.hasTag("fun"));
        assertEquals("[T][ ] read book [#fun]", t.toString());
    }

    @Test
    void removeTag_removesIfPresent() {
        Task t = new Todo("read book");
        try {
            t.addTag("#fun");
        } catch (HuhhhException e) {
            // This should not happen in this test
            assert false : "Unexpected exception: " + e.getMessage();
        }
        assertTrue(t.hasTag("fun"));
        try {
            t.removeTag("#fun");
        } catch (HuhhhException e) {
            assert false : "Unexpected exception: " + e.getMessage();
        }
        assertFalse(t.hasTag("#fun"));
        assertEquals("[T][ ] read book", t.toString());
    }

    @Test
    void addTag_invalid_throws() {
        Task t = new Todo("read book");
        assertThrows(HuhhhException.class, () -> t.addTag("#bad tag"));
        assertThrows(HuhhhException.class, () -> t.addTag("#"));
        assertThrows(HuhhhException.class, () -> t.addTag("fun"));
    }
}
