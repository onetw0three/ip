package huhhh.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import huhhh.HuhhhException;
import huhhh.task.Deadline;
import huhhh.task.Task;

public class StorageTest {
    private static Path createTempFileWithContent(String content) throws Exception {
        Path saveFile = Files.createTempFile("huhhh-storage-", ".txt");
        Files.writeString(saveFile, content, StandardCharsets.UTF_8);
        return saveFile;
    }

    @Test
    void load_emptyFile_returnsEmptyList() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("\n\n   \n"));
        List<Task> loaded = storage.load();
        Assertions.assertTrue(loaded.isEmpty());
    }

    @Test
    void load_corruptedEntryTooShort_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("T | 0\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertTrue(ex.getMessage().startsWith("Corrupted save entry:"));
    }

    @Test
    void load_unknownTaskType_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("X | 0 | something\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertEquals("Unknown task type in save: X", ex.getMessage());
    }

    @Test
    void load_invalidCompletionFlag_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("T | 2 | something\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertTrue(ex.getMessage().startsWith("Invalid completion flag in entry:"));
    }

    @Test
    void load_deadlineMissingDatePart_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("D | 0 | return book\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertTrue(ex.getMessage().startsWith("Corrupted deadline entry:"));
    }

    @Test
    void load_deadlineWithInvalidDate_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("D | 0 | return book | not-a-date\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertTrue(ex.getMessage().startsWith("Corrupted deadline date:"));
    }

    @Test
    void load_eventMissingParts_throws() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("E | 0 | trip | 2026-01-01\n"));
        HuhhhException ex = assertThrows(HuhhhException.class, storage::load);
        assertTrue(ex.getMessage().startsWith("Corrupted event entry:"));
    }

    @Test
    void parse_deadlineWithValidDate_returnsDeadlineTask() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("D | 1 | submit report | 2024-12-31\n"));
        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        Task task = loaded.get(0);
        assertInstanceOf(Deadline.class, task);
        huhhh.task.Deadline deadline = (huhhh.task.Deadline) task;
        assertEquals("[D][X] submit report (by: Dec 31 2024)", deadline.toString());
    }

    @Test
    void load_todoWithTags_loadsTags() throws Exception {
        Storage storage = new Storage(createTempFileWithContent("T | 0 | read book | fun,School\n"));
        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book [#fun #school]", loaded.get(0).toString());
    }
}
