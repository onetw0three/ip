package huhhh.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    protected LocalDate by;

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
        return String.format("D | %d | %s | %s", isDone ? 1 : 0, description, by.format(STORAGE_FORMAT));
    }
}