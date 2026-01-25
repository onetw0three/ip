package huhhh.command;

import huhhh.HuhhhException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

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

    public static int parseIndex(String input) throws HuhhhException {
        try {
            return Integer.parseInt(input.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new HuhhhException(String.format("Invalid task index provided: %s.", input));
        }
    }

    public static LocalDate parseDate(String rawDate) throws HuhhhException {
        try {
            return LocalDate.parse(rawDate.trim());
        } catch (DateTimeParseException e) {
            throw new HuhhhException("Dates must follow the yyyy-mm-dd format (e.g., 2019-10-15).");
        }
    }

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
