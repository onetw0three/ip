package huhhh.ui;

import java.util.Scanner;

import huhhh.task.Task;
import huhhh.task.TaskList;

/**
 * Ui class handles all interactions with the user, including displaying messages
 * and reading user input.
 */
public class Ui {
    private static final String DIVIDER = "    ____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message to the user.
     */
    public void showWelcome() {
        showMessage("Hello! I'm Huhhh\nWhat can I do for you?");
    }

    /**
     * Displays the goodbye message to the user.
     */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a message indicating that a task has been added.
     *
     * @param task The task that was added.
     * @param totalTasks The total number of tasks after addition.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showMessage("Got it. I've added this task:\n  " + task
                + "\nNow you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Displays a message indicating that a task has been removed.
     *
     * @param task The task that was removed.
     * @param remainingTasks The number of tasks remaining after removal.
     */
    public void showTaskRemoved(Task task, int remainingTasks) {
        showMessage("Noted. I've removed this task:\n " + task
                + "\nNow you have " + remainingTasks + " tasks in the list.");
    }

    /**
     * Displays a message indicating that a task has been marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        showMessage("Nice! I've marked this task as done:\n  " + task);
    }

    /**
     * Displays a message indicating that a task has been unmarked as not done.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showMessage("OK, I've marked this task as not done yet:\n  " + task);
    }

    /**
     * Displays the list of tasks to the user.
     *
     * @param tasks The TaskList containing the tasks to be displayed.
     */
    public void showTasks(TaskList tasks) {
        showMessage("Here are the tasks in your list:\n" + tasks);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to be displayed.
     */
    public void showError(String message) {
        System.err.println(formatMessage(message));
    }

    /**
     * Displays a loading error message to the user.
     *
     * @param message The error message related to loading tasks.
     */
    public void showLoadingError(String message) {
        showError("Unable to load previous tasks, starting with an empty list.\n" + message);
    }

    /**
     * Displays a general message to the user.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(String message) {
        System.out.println(formatMessage(message));
    }

    /**
     * Displays the list of tasks found to the user
     *
     * @param foundTasks List of tasks that are found from the search
     */
    public void showFoundTasks(TaskList foundTasks) {
        showMessage("Here are the matching tasks in your list:\n" + foundTasks);
    }

    /**
     * Displays a divider line for better readability.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Reads a command input from the user.
     *
     * @return The command input as a String.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    /**
     * Closes the scanner resource.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Formats a message with dividers for better readability.
     *
     * @param message The message to be formatted.
     * @return The formatted message as a String.
     */
    private String formatMessage(String message) {
        return DIVIDER + "\n     " + message.replace("\n", "\n     ") + "\n" + DIVIDER + "\n";
    }
}
