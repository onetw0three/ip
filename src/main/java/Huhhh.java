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
                msg.replace("\n", "\n     ")
            );
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

    private static void addTodo(String msg) {
        int startIndex = msg.indexOf(" ") + 1;
        String desc = msg.substring(startIndex).trim();
        addTask(new Todo(desc));
    }

    private static void addDeadline(String msg) {
        int startIndex = msg.indexOf(" ") + 1;
        int byIndex = msg.indexOf("/by");
        String desc = msg.substring(startIndex, byIndex).trim();
        String by = msg.substring(byIndex + 3).trim();
        addTask(new Deadline(desc, by));

    }

    private static void addEvent(String msg) {
        int startIndex = msg.indexOf(" ") + 1;
        int fromIndex = msg.indexOf("/from");
        int toIndex = msg.indexOf("/to");
        String desc = msg.substring(startIndex, fromIndex).trim();
        String from = msg.substring(fromIndex + 5, toIndex).trim();
        String to = msg.substring(toIndex + 3).trim();
        addTask(new Event(desc, from, to));
    }


    private static void mark(int idx) {
        tasks.get(idx).markAsDone();
        System.out.println(formatMessage(
                "Nice! I've marked this task as done:\n  " + tasks.get(idx)));
    }

    private static void unmark(int idx) {
        tasks.get(idx).markUndone();
        System.out.println(formatMessage(
                "OK, I've marked this task as not done yet:\n  " + tasks.get(idx)));
    }

    private static void list() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            String line = String.format("%d. %s\n", i + 1, tasks.get(i));
            out.append(line);
        }
        System.out.println(formatMessage(out.toString().trim()));
    }

    private static void goodbye() {
        System.out.println(formatMessage("Bye. Hope to see you again soon!"));
    }

    private static int parseIndex(String input) {
        String[] args = input.split(" ");
        return Integer.parseInt(args[1]) - 1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        greet();
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                goodbye();
                break;
            } else if (input.equals("list")) {
                list();
            } else if (input.startsWith("mark ")){
                mark(parseIndex(input));
            } else if (input.startsWith("unmark ")){
                unmark(parseIndex(input));
            } else if (input.startsWith("todo ")){
                addTodo(input);
            } else if (input.startsWith("deadline ")){
                addDeadline(input);
            } else if (input.startsWith("event ")){
                addEvent(input);
            }
        }
    }
}