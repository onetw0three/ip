import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Huhhh {
    private static List<Task> tasks = new ArrayList<>();

    private static String formatMessage(String msg) {
        return String.format(
                "    ____________________________________________________________\n" +
                        "     %s\n" +
                        "    ____________________________________________________________\n",
                msg.replace("\n", "\n     "));
    }

    private static void greet() {
        String welcomeMsg = "Hello! I'm Huhhh\nWhat can I do for you?";
        System.out.println(formatMessage(welcomeMsg));
    }

    private static void addTask(Task newTask) {
        tasks.add(newTask);
        System.out.println(formatMessage(
                "Got it. I've added this task:\n  " + newTask +
                        "\nNow you have " + tasks.size() + " tasks in the list."));
    }

    private static void addTodo(String msg) throws HuhhhException {
        if (msg.trim().isEmpty()) {
            throw new HuhhhException("Todo task must have a description.\nUsage: todo <desc>");
        }
        addTask(new Todo(msg));
    }

    private static void addDeadline(String msg) throws HuhhhException {
        int byIndex = msg.indexOf("/by");
        if (byIndex == -1) {
            throw new HuhhhException("Deadline task must have a /by clause.\nUsage: deadline <desc> /by <date>");
        }
        String desc = msg.substring(0, byIndex).trim();
        if (desc.isEmpty()) {
            throw new HuhhhException("Deadline task must have a description.\nUsage: deadline <desc> /by <date>");
        }
        String by = msg.substring(byIndex + 3).trim();
        if (by.isEmpty()) {
            throw new HuhhhException(
                    "Deadline task must have a specified /by date.\nUsage: deadline <desc> /by <date>");
        }
        addTask(new Deadline(desc, by));

    }

    private static void addEvent(String msg) throws HuhhhException {
        int fromIndex = msg.indexOf("/from");
        int toIndex = msg.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1) {
            throw new HuhhhException(
                    "Event task must have /from and /to clauses.\nUsage: event <desc> /from <date> /to <date>");
        }
        if (fromIndex >= toIndex) {
            throw new HuhhhException(
                    "/from clause must come before /to clause.\nUsage: event <desc> /from <date> /to <date>");
        }
        String desc = msg.substring(0, fromIndex).trim();
        if (desc.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a description.\nUsage: event <desc> /from <date> /to <date>");
        }
        String from = msg.substring(fromIndex + 5, toIndex).trim();
        if (from.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a specified /from date.\nUsage: event <desc> /from <date> /to <date>");
        }
        String to = msg.substring(toIndex + 3).trim();
        if (to.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a specified /to date.\nUsage: event <desc> /from <date> /to <date>");
        }
        addTask(new Event(desc, from, to));
    }

    private static void mark(int idx) throws HuhhhException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new HuhhhException("Task index out of bounds. You have " + tasks.size() + " tasks.");
        }
        tasks.get(idx).markAsDone();
        System.out.println(formatMessage("Nice! I've marked this task as done:\n  " + tasks.get(idx)));
    }

    private static void unmark(int idx) throws HuhhhException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new HuhhhException("Task index out of bounds. You have " + tasks.size() + " tasks.");
        }
        tasks.get(idx).markUndone();
        System.out.println(formatMessage("OK, I've marked this task as not done yet:\n  " + tasks.get(idx)));
    }

    private static void delete(int idx) throws HuhhhException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new HuhhhException("Task index out of bounds. You have " + tasks.size() + " tasks.");
        }
        Task removed = tasks.remove(idx);
        System.out.println(formatMessage(
            "Noted. I've removed this task:\n " + removed +
            "\nNow you have " + tasks.size() + " tasks in the list."));
    }

    private static void list() {
        if (tasks.isEmpty()) {
            System.out.println(formatMessage("You have no tasks in your list."));
            return;
        }
        StringBuilder out = new StringBuilder();
        out.append("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            String line = String.format("%d. %s\n", i + 1, tasks.get(i));
            out.append(line);
        }
        System.out.println(formatMessage(out.toString().trim()));
    }

    private static void goodbye() {
        System.out.println(formatMessage("Bye. Hope to see you again soon!"));
    }

    private static int parseIndex(String input) throws HuhhhException {
        try {
            return Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            throw new HuhhhException(String.format("Invalid task index provided: %s.", input));
        }
    }

    private static void processCommand(String input) throws HuhhhException {
        String[] args = input.trim().split(" ", 2);
        Command command = Command.fromString(args[0]);
        String desc = args.length > 1 ? args[1] : "";
        switch (command) {
            case LIST:
                list();
                break;
            case MARK:
                mark(parseIndex(desc));
                break;
            case UNMARK:
                unmark(parseIndex(desc));
                break;
            case DELETE:
                delete(parseIndex(desc));
                break;
            case TODO:
                addTodo(desc);
                break;
            case DEADLINE:
                addDeadline(desc);
                break;
            case EVENT:
                addEvent(desc);
                break;
            default:
                throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        greet();
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                goodbye();
                break;
            }
            try {
                processCommand(input);
            } catch (HuhhhException e) {
                System.err.println(formatMessage(e.getMessage()));
            }
        }
        scanner.close();
    }
}