package huhhh.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import huhhh.HuhhhException;

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
        assert parts.length >= 1 : "Splitting a non-empty command should yield at least 1 token";

        Command command = Command.fromString(parts[0]);
        if (command == Command.UNKNOWN) {
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
        String arguments = parts.length > 1 ? parts[1] : "";
        ParsedCommand parsed = new ParsedCommand(command, arguments);
        assert parsed.getCommand() != null && parsed.getCommand() != Command.UNKNOWN
                : "Parsed command should be a known command";
        return parsed;
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

    /**
     * Parses a date string in the format yyyy-mm-dd.
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
     * Parses arguments in the form: "index #tag [#tag...]".
     *
     * @param rawArguments The raw argument string.
     * @return A pair of (0-based index, list of raw tag tokens).
     * @throws HuhhhException If parsing fails.
     */
    public static ParsedIndexAndTags parseIndexAndTags(String rawArguments) throws HuhhhException {
        if (rawArguments == null || rawArguments.trim().isEmpty()) {
            throw new HuhhhException("Expected an index and at least one tag. Usage: (un)tag <index> #tag [#tag...]");
        }
        String[] parts = rawArguments.trim().split("\\s+");
        if (parts.length < 2) {
            throw new HuhhhException("Expected an index and at least one tag. Usage: (un)tag <index> #tag [#tag...]");
        }
        int index = parseIndex(parts[0]);
        List<String> tags = Arrays.stream(parts)
                .skip(1)
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
        for (String t : tags) {
            if (!t.startsWith("#")) {
                throw new HuhhhException("Tags must start with '#'. Invalid tag: " + t);
            }
        }
        return new ParsedIndexAndTags(index, tags);
    }

    /**
     * Most basic structure to hold a parsed command and its arguments.
     */
    public static class ParsedCommand {
        private final Command command;
        private final String arguments;

        /**
         * Constructs a ParsedCommand with the given command and arguments.
         *
         * @param command   The parsed command.
         * @param arguments The arguments associated with the command.
         */
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

    /**
     * Holder for an index + tags argument parse.
     */
    public static class ParsedIndexAndTags {
        private final int index;
        private final List<String> tags;

        /**
         * Constructs a ParsedIndexAndTags with the given index and tags.
         * @param index 0-based index
         * @param tags List of raw tag tokens
         */
        public ParsedIndexAndTags(int index, List<String> tags) {
            this.index = index;
            this.tags = tags;
        }

        public int getIndex() {
            return index;
        }

        public List<String> getTags() {
            return tags;
        }
    }
}
