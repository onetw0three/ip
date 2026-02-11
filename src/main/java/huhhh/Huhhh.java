package huhhh;

import java.nio.file.Paths;
import java.time.LocalDate;

import huhhh.command.Parser;
import huhhh.storage.Storage;
import huhhh.task.Deadline;
import huhhh.task.Event;
import huhhh.task.Task;
import huhhh.task.TaskList;
import huhhh.task.Todo;
import huhhh.ui.Logger;

/**
 * The main application class for Huhhh task manager.
 * It handles user interaction, command parsing, task management,
 * and data persistence.
 */
public class Huhhh {
    private final Storage storage;
    private final TaskList tasks;

    private boolean isExit = false;

    public Huhhh() {
        this(new Storage());
    }

    /**
     * Constructs a Huhhh application with the specified file path for storage.
     * If the file path is null or blank, uses the default storage location.
     *
     * @param filePath The file path for storing tasks.
     */
    public Huhhh(String filePath) {
        this(filePath == null || filePath.isBlank()
                ? new Storage()
                : new Storage(Paths.get(filePath)));
    }

    private Huhhh(Storage storage) {
        this.storage = storage;
        this.tasks = loadTasks();
    }

    /**
     * Processes a single line of user input and returns the text response.
     * This is the main entry-point used by the JavaFX GUI.
     *
     * @param input User input.
     * @return Response text to display.
     */
    public String getResponse(String input) {
        if (isExit) {
            return "Bye. Hope to see you again soon!";
        }

        try {
            Parser.ParsedCommand parsedCommand = Parser.parse(input);
            return executeForResponse(parsedCommand);
        } catch (HuhhhException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns true if the app has received a BYE command and should exit.
     * This is primarily used by the JavaFX GUI to decide when to close the window.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Loads tasks from storage. If loading fails, initializes an empty task list
     *
     * @return The loaded TaskList or an empty TaskList if loading fails.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (HuhhhException e) {
            Logger.showLoadingError(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Dispatches and executes the given command (GUI path: returns strings).
     *
     * @param parsedCommand The command to execute.
     * @return The response string to be displayed.
     * @throws HuhhhException If an error occurs during command execution.
     */
    private String executeForResponse(Parser.ParsedCommand parsedCommand) throws HuhhhException {
        switch (parsedCommand.getCommand()) {
        case LIST:
            return "Here are the tasks in your list:\n" + tasks;
        case MARK: {
            Task task = tasks.mark(Parser.parseIndex(parsedCommand.getArguments()));
            persistTasks();
            return "Nice! I've marked this task as done:\n  " + task;
        }
        case UNMARK: {
            Task task = tasks.unmark(Parser.parseIndex(parsedCommand.getArguments()));
            persistTasks();
            return "OK, I've marked this task as not done yet:\n  " + task;
        }
        case DELETE: {
            Task task = tasks.delete(Parser.parseIndex(parsedCommand.getArguments()));
            persistTasks();
            return "Noted. I've removed this task:\n " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case TODO: {
            Task task = createTodo(parsedCommand.getArguments());
            addTask(task);
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case DEADLINE: {
            Task task = createDeadline(parsedCommand.getArguments());
            addTask(task);
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case EVENT: {
            Task task = createEvent(parsedCommand.getArguments());
            addTask(task);
            return "Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case FIND: {
            String keyword = parsedCommand.getArguments().trim();
            if (keyword.isEmpty()) {
                throw new HuhhhException("Find command requires a keyword to search for.\nUsage: find <keyword>");
            }
            if (keyword.startsWith("#")) {
                return "Here are the matching tasks with the tags:\n" + tasks.findTasksByTag(keyword);
            }
            return "Here are the matching tasks in your list:\n" + tasks.findTasks(keyword);
        }
        case TAG: {
            Parser.ParsedIndexAndTags parsed = Parser.parseIndexAndTags(parsedCommand.getArguments());
            Task task = tasks.tag(parsed.getIndex(), parsed.getTags());
            persistTasks();
            return "Noted. I've tagged this task:\n  " + task;
        }
        case UNTAG: {
            Parser.ParsedIndexAndTags parsed = Parser.parseIndexAndTags(parsedCommand.getArguments());
            Task task = tasks.untag(parsed.getIndex(), parsed.getTags());
            persistTasks();
            return "Noted. I've removed tag(s) from this task:\n  " + task;
        }
        case BYE:
            isExit = true;
            return "Bye. Hope to see you again soon!";
        default:
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
    }

    private Task createTodo(String arguments) throws HuhhhException {
        Task.ParsedTextWithTags parsed = Task.parseDescriptionAndTags(arguments);
        String description = parsed.getText().trim();
        if (description.isEmpty()) {
            throw new HuhhhException("Todo task must have a description.\nUsage: todo <desc>");
        }
        assert !description.isBlank() : "Todo description should be non-blank after validation";
        Task task = new Todo(description);
        for (String t : parsed.getTags()) {
            task.addTag(t);
        }
        return task;
    }

    private Task createDeadline(String arguments) throws HuhhhException {
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new HuhhhException("Deadline task must have a /by clause.\n"
                    + "Usage: deadline <desc> /by <date>");
        }

        String descRaw = arguments.substring(0, byIndex).trim();
        Task.ParsedTextWithTags parsed = Task.parseDescriptionAndTags(descRaw);
        String desc = parsed.getText().trim();
        if (desc.isEmpty()) {
            throw new HuhhhException("Deadline task must have a description.\n"
                    + "Usage: deadline <desc> /by <date>");
        }
        String by = arguments.substring(byIndex + 3).trim();
        if (by.isEmpty()) {
            throw new HuhhhException("Deadline task must have a specified /by date.\n"
                    + "Usage: deadline <desc> /by <date>");
        }
        LocalDate dueDate = Parser.parseDate(by);
        Task task = new Deadline(desc, dueDate);
        for (String t : parsed.getTags()) {
            task.addTag(t);
        }
        return task;
    }

    private Task createEvent(String arguments) throws HuhhhException {
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1) {
            throw new HuhhhException(
                    "Event task must have /from and /to clauses.\nUsage: event <desc> /from <date> /to <date>");
        }
        if (fromIndex >= toIndex) {
            throw new HuhhhException(
                    "/from clause must come before /to clause.\nUsage: event <desc> /from <date> /to <date>");
        }

        String descRaw = arguments.substring(0, fromIndex).trim();
        Task.ParsedTextWithTags parsed = Task.parseDescriptionAndTags(descRaw);
        String desc = parsed.getText().trim();
        if (desc.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a description.\nUsage: event <desc> /from <date> /to <date>");
        }

        String from = arguments.substring(fromIndex + 5, toIndex).trim();
        if (from.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a specified /from date.\nUsage: event <desc> /from <date> /to <date>");
        }
        String to = arguments.substring(toIndex + 3).trim();
        if (to.isEmpty()) {
            throw new HuhhhException(
                    "Event task must have a specified /to date.\nUsage: event <desc> /from <date> /to <date>");
        }
        assert !from.isBlank() && !to.isBlank() : "Event from/to should be non-blank after validation";
        Task task = new Event(desc, from, to);

        for (String t : parsed.getTags()) {
            task.addTag(t);
        }
        return task;
    }

    /**
     * Adds a task to the task list, updates storage, and shows confirmation.
     *
     * @param task The task to add.
     * @throws HuhhhException If an error occurs during addition.
     */
    private void addTask(Task task) throws HuhhhException {
        tasks.add(task);
        persistTasks();
    }

    /**
     * Persists the current task list to storage.
     *
     * @throws HuhhhException If an error occurs during saving.
     */
    private void persistTasks() throws HuhhhException {
        storage.save(tasks);
    }
}
