package huhhh.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import huhhh.HuhhhException;

public class ParserTest {

    private static String anyKnownCommandToken() {
        for (Command c : Command.values()) {
            String token = c.name().toLowerCase();
            if (Command.fromString(token) != Command.UNKNOWN) {
                return token;
            }
        }
        fail("No known command tokens found in Command enum.");
        return null;
    }

    @Test
    void parse_nullInput_throws() {
        HuhhhException ex = assertThrows(HuhhhException.class, () -> Parser.parse(null));
        assertEquals("Command cannot be null.", ex.getMessage());
    }

    @Test
    void parse_emptyInput_throws() {
        HuhhhException ex1 = assertThrows(HuhhhException.class, () -> Parser.parse(""));
        assertEquals("Command cannot be empty.", ex1.getMessage());

        HuhhhException ex2 = assertThrows(HuhhhException.class, () -> Parser.parse("   "));
        assertEquals("Command cannot be empty.", ex2.getMessage());
    }

    @Test
    void parse_unknownCommand_throws() {
        HuhhhException ex = assertThrows(HuhhhException.class, () -> Parser.parse("abc 123"));
        assertEquals("I'm sorry, but I don't know what that means :(", ex.getMessage());
    }

    @Test
    void parse_knownCommandWithoutArgs_returnsParsedCommand() throws Exception {
        String token = anyKnownCommandToken();

        Parser.ParsedCommand parsed = Parser.parse(token);

        assertNotNull(parsed);
        assertEquals(Command.fromString(token), parsed.getCommand());
        assertEquals("", parsed.getArguments());
    }

    @Test
    void parse_knownCommandWithArgs_splitsIntoCommandAndArguments() throws Exception {
        String token = anyKnownCommandToken();

        Parser.ParsedCommand parsed = Parser.parse(token + "   abc def");

        assertEquals(Command.fromString(token), parsed.getCommand());
        // Parser splits with limit=2, so the argument keeps the leading spaces after the first space.
        assertEquals("  abc def", parsed.getArguments());
    }

    @Test
    void parseIndex_validIndex_returnsZeroBased() throws Exception {
        assertEquals(0, Parser.parseIndex("1"));
        assertEquals(1, Parser.parseIndex("2"));
        assertEquals(9, Parser.parseIndex("10"));
        assertEquals(2, Parser.parseIndex(" 3 "));
    }

    @Test
    void parseIndex_invalid_throws() {
        HuhhhException ex = assertThrows(HuhhhException.class, () -> Parser.parseIndex("abc"));
        assertEquals("Invalid task index provided: abc.", ex.getMessage());
    }

    @Test
    void parseDate_valid_returnsLocalDate() throws Exception {
        LocalDate date = Parser.parseDate("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), date);

        LocalDate date2 = Parser.parseDate(" 2020-01-02 ");
        assertEquals(LocalDate.of(2020, 1, 2), date2);
    }

    @Test
    void parseDate_invalid_throws() {
        HuhhhException ex = assertThrows(HuhhhException.class, () -> Parser.parseDate("25-01-2026"));
        assertEquals("Dates must follow the yyyy-mm-dd format (e.g., 2019-10-15).", ex.getMessage());
    }
}
