package huhhh.task;

import huhhh.HuhhhException;
import java.util.ArrayList;
import java.util.List;

public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int index) throws HuhhhException {
        Task task = getTask(index);
        tasks.remove(index);
        return task;
    }

    public Task mark(int index) throws HuhhhException {
        Task task = getTask(index);
        task.markAsDone();
        return task;
    }

    public Task unmark(int index) throws HuhhhException {
        Task task = getTask(index);
        task.markUndone();
        return task;
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    private Task getTask(int index) throws HuhhhException {
        if (index < 0 || index >= tasks.size()) {
            throw new HuhhhException("Task index out of bounds. You have " + tasks.size() + " tasks.");
        }
        return tasks.get(index);
    }

    public TaskList findTasks(String keyword) {
        return new TaskList(tasks.stream().
                filter(task -> task.containsKeyword(keyword))
                .toList());
    }

    public List<String> serialisedList() {
        List<String> serialized = new ArrayList<>();
        for (Task task : tasks) {
            serialized.add(task.serialisedString());
        }
        return serialized;
    }

    public String toString() {
        if (tasks.isEmpty()) {
            return "You have no tasks in your list.";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            builder.append(i + 1).append(". ").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}
