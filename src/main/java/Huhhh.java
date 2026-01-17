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

    private static void add(String msg) {
        tasks.add(new Task(msg));
        System.out.println(formatMessage("added: " + msg));
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
            } else {
                add(input);
            }
        }
    }
}