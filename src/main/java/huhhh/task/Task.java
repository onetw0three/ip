package huhhh.task;

/**
 * Represents a general task with a description and completion status.
 * To be used as a base class for specific task types.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
