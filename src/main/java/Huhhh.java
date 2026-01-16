import java.util.Scanner;

public class Huhhh {
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

    private static void echo(String msg) {
        System.out.println(formatMessage(msg));
    }

    private static void goodbye() {
        System.out.println(formatMessage("Bye. Hope to see you again soon!"));
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
            echo(input);
        }
    }
}