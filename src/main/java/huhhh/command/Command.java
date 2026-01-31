package huhhh.command;

/**
 * Enum representing the various commands supported by the Huhhh application.
 */
public enum Command {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, BYE, UNKNOWN;

    /**
     * Converts a string input to its corresponding Command enum value.
     * If the input does not match any known command, UNKNOWN is returned.
     *
     * @param input The string input representing the command.
     * @return The corresponding Command enum value.
     */
    public static Command fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return Command.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
