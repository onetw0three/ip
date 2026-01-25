package huhhh.ui;

import huhhh.task.Task;
import huhhh.task.TaskList;
import java.util.Scanner;

public class Ui {

    private static final String DIVIDER = "    ____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        showMessage("Hello! I'm Huhhh\nWhat can I do for you?");
    }

    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    public void showTaskAdded(Task task, int totalTasks) {
        showMessage("Got it. I've added this task:\n  " + task
                + "\nNow you have " + totalTasks + " tasks in the list.");
    }

    public void showTaskRemoved(Task task, int remainingTasks) {
        showMessage("Noted. I've removed this task:\n " + task
                + "\nNow you have " + remainingTasks + " tasks in the list.");
    }

    public void showTaskMarked(Task task) {
        showMessage("Nice! I've marked this task as done:\n  " + task);
    }

    public void showTaskUnmarked(Task task) {
        showMessage("OK, I've marked this task as not done yet:\n  " + task);
    }

    public void showTasks(TaskList tasks) {
        showMessage(tasks.toString());
    }

    public void showError(String message) {
        System.err.println(formatMessage(message));
    }

    public void showLoadingError(String message) {
        showError("Unable to load previous tasks, starting with an empty list.\n" + message);
    }

    public void showMessage(String message) {
        System.out.println(formatMessage(message));
    }

    public void showLine() {
        System.out.println(DIVIDER);
    }

    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }

    private String formatMessage(String message) {
        return DIVIDER + "\n     " + message.replace("\n", "\n     ") + "\n" + DIVIDER + "\n";
    }
}
