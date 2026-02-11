package huhhh.task;

/**
 * Represents an event task with a description, start time, and end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Constructs an Event with the given description, start time, and end time.
     *
     * @param description The description of the event.
     * @param from        The start time of the event.
     * @param to          The end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String serialisedString() {
        String tagsField = serialisedTagsField();
        if (tagsField.isEmpty()) {
            return String.format("E | %d | %s | %s | %s",
                    super.isDone() ? 1 : 0, super.getDescription(), from, to);
        }
        return String.format("E | %d | %s | %s | %s | %s",
                super.isDone() ? 1 : 0, super.getDescription(), from, to, tagsField);
    }
}
