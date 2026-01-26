package huhhh.command;

public enum Command {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    BYE,
    UNKNOWN;

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