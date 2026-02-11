package huhhh.task;

/**
 * Represents a todo task with a description.
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String serialisedString() {
        return String.format("T | %d | %s",
                super.isDone() ? 1 : 0, super.getDescription());
    }
}
