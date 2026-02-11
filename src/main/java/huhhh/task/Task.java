package huhhh.task;

/**
 * Represents a general task with a description and completion status.
 * To be used as a base class for specific task types.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Constructs a Task with the given description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns true if the task is marked done.
     */
    protected boolean isDone() {
        return isDone;
    }

    /**
     * Returns true if {@code keyword} is contained in the task description.
     *
     * @param keyword String to search for in the description.
     * @return true if the keyword is found in the description.
     */
    public boolean containsKeyword(String keyword) {
        if (keyword == null) {
            return false;
        }
        return description.contains(keyword);
    }

    /**
     * Returns the status icon representing whether the task is done.
     *
     * @return "X" if the task is done, otherwise a space " ".
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }

    /**
     * Serialises the task into a string format suitable for storage.
     *
     * @return The serialised string representation of the task.
     */
    public abstract String serialisedString();
}
