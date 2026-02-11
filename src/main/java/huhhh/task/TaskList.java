package huhhh.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import huhhh.HuhhhException;

/**
 * Represents a list of tasks and provides methods to manipulate them.
 * Methods include add , delete, mark, unmark, and serialize tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the task list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes a task from the task list by its index.
     *
     * @param index The index of the task to be deleted.
     * @return The deleted task.
     * @throws HuhhhException if the index is out of bounds.
     */
    public Task delete(int index) throws HuhhhException {
        Task task = getTask(index);
        tasks.remove(index);
        return task;
    }

    /**
     * Marks a task as done by its index.
     *
     * @param index The index of the task to be marked.
     * @return The marked task.
     * @throws HuhhhException if the index is out of bounds.
     */
    public Task mark(int index) throws HuhhhException {
        Task task = getTask(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks a task as not done by its index.
     *
     * @param index The index of the task to be unmarked.
     * @return The unmarked task.
     * @throws HuhhhException if the index is out of bounds.
     */
    public Task unmark(int index) throws HuhhhException {
        Task task = getTask(index);
        task.markUndone();
        return task;
    }

    /**
     * Adds the given tag(s) to a task.
     *
     * @param index Task index (0-based)
     * @param rawTags Tag tokens like "#fun"
     * @return The updated task.
     * @throws HuhhhException if index is out of bounds or a tag is invalid.
     */
    public Task tag(int index, List<String> rawTags) throws HuhhhException {
        Task task = getTask(index);
        if (rawTags == null || rawTags.isEmpty()) {
            throw new HuhhhException("Tag command requires at least one tag.\nUsage: tag <index> #tag [#tag...] ");
        }
        try {
            for (String t : rawTags) {
                task.addTag(t);
            }
        } catch (IllegalArgumentException e) {
            throw new HuhhhException(e.getMessage());
        }
        return task;
    }

    /**
     * Removes the given tag(s) from a task.
     *
     * @param index Task index (0-based)
     * @param rawTags Tag tokens like "#fun"
     * @return The updated task.
     * @throws HuhhhException if index is out of bounds or a tag is invalid.
     */
    public Task untag(int index, List<String> rawTags) throws HuhhhException {
        Task task = getTask(index);
        if (rawTags == null || rawTags.isEmpty()) {
            throw new HuhhhException(
                    "Untag command requires at least one tag.\nUsage: untag <index> #tag [#tag...] ");
        }
        try {
            for (String t : rawTags) {
                task.removeTag(t);
            }
        } catch (IllegalArgumentException e) {
            throw new HuhhhException(e.getMessage());
        }
        return task;
    }

    /**
     * Returns the number of tasks in the task list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return true if the task list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Retrieves a task by its index, with bounds checking.
     *
     * @param index The index of the task to retrieve.
     * @return The task at the specified index.
     * @throws HuhhhException if the index is out of bounds.
     */
    private Task getTask(int index) throws HuhhhException {
        if (index < 0 || index >= tasks.size()) {
            throw new HuhhhException("Task index out of bounds. You have " + tasks.size() + " tasks.");
        }
        return tasks.get(index);
    }

    /**
     * Retrieves all the tasks that have a matching keyword in
     * its description.
     *
     * @param keyword String to match in the description
     * @return A TaskList containing matching tasks.
     */
    public TaskList findTasks(String keyword) {
        return new TaskList(tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList());
    }

    /**
     * Retrieves all the tasks that have the given tag.
     *
     * @param rawTag Tag token like "#fun" (leading '#' optional)
     * @return A TaskList containing matching tasks.
     */
    public TaskList findTasksByTag(String rawTag) {
        return new TaskList(tasks.stream()
                .filter(task -> task.hasTag(rawTag))
                .toList());
    }

    /**
     * Serializes the task list into a list of strings for storage.
     *
     * @return A list of serialized task strings.
     */
    public List<String> serialisedList() {
        return tasks.stream()
                .map(Task::serialisedString)
                .toList();
    }

    @Override
    public String toString() {
        if (tasks.isEmpty()) {
            return "You have no tasks in your list.";
        }

        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.joining("\n"));
    }
}
