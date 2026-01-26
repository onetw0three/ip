package huhhh.command;

import huhhh.HuhhhException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parser class to handle parsing of user input commands and arguments.
 */
public class Parser {

    /**
     * Parses the full command string into a ParsedCommand object which can be
     * used to get the command and its arguments.
     *
     * @param fullCommand The full command string from user input.
     * @return A ParsedCommand object containing the command and its arguments.
     * @throws HuhhhException If the command is unknown or invalid.
     */
    public static ParsedCommand parse(String fullCommand) throws HuhhhException {
        if (fullCommand == null) {
            throw new HuhhhException("Command cannot be null.");
        }
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new HuhhhException("Command cannot be empty.");
        }
        String[] parts = trimmed.split(" ", 2);
        Command command = Command.fromString(parts[0]);
        if (command == Command.UNKNOWN) {
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
        String arguments = parts.length > 1 ? parts[1] : "";
        return new ParsedCommand(command, arguments);
    }

    /**
     * Parses a 1-based task index from user input into a 0-based index.
     *
     * @param input The user input representing the task index.
     * @return The parsed 0-based task index.
     * @throws HuhhhException If the input is not a valid integer.
     */
    public static int parseIndex(String input) throws HuhhhException {
        try {
            return Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new HuhhhException(String.format("Invalid task index provided: %s.", input));
        }
    }

    /** Parses a date string in the format yyyy-mm-dd.
     *
     * @param rawDate The raw date string from user input.
     * @return The parsed LocalDate object.
     * @throws HuhhhException If the date format is invalid.
     */
    public static LocalDate parseDate(String rawDate) throws HuhhhException {
        try {
            return LocalDate.parse(rawDate.trim());
        } catch (DateTimeParseException e) {
            throw new HuhhhException("Dates must follow the yyyy-mm-dd format (e.g., 2019-10-15).");
        }
    }

    /**
     * Most basic structure to hold a parsed command and its arguments.
     */
    public static class ParsedCommand {
        private final Command command;
        private final String arguments;

        public ParsedCommand(Command command, String arguments) {
            this.command = command;
            this.arguments = arguments;
        }

        public Command getCommand() {
            return command;
        }

        public String getArguments() {
            return arguments;
        }
    }
}
