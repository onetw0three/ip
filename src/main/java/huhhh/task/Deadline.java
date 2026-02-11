package huhhh.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task with a description and a due date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    protected LocalDate by;

    /**
     * Constructs a Deadline with the given description and due date.
     *
     * @param description The description of the deadline.
     * @param by          The due date of the deadline.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String serialisedString() {
        return String.format("D | %d | %s | %s",
                super.isDone() ? 1 : 0,
                super.getDescription(),
                by.format(STORAGE_FORMAT));
    }
}
