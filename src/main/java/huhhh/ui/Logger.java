package huhhh.ui;

/**
 * * A simple logger for displaying error messages to the standard error stream.
 */
public class Logger {
    public static void showError(String message) {
        System.err.println("[!] " + message);
    }

    public static void showLoadingError(String message) {
        showError("Unable to load previous tasks, starting with an empty list.\n" + message);
    }
}
