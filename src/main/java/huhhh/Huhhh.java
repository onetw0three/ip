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
import huhhh.ui.Ui;

/**
 * The main application class for Huhhh task manager.
 * It handles user interaction, command parsing, task management,
 * and data persistence.
 */
public class Huhhh {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

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
        this.ui = new Ui();
        this.storage = storage;
        this.tasks = loadTasks();
    }

    /**
     * Processes a single line of user input and returns the text response.
     *
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
     *
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
            ui.showLoadingError(e.getMessage());
            return new TaskList();
        }
    }

    /**
     * Starts the main interaction loop of the application. Handles user input,
     * until BYE command is received.
     */
    public void run() {
        ui.showWelcome();
        while (!isExit) {
            try {
                Parser.ParsedCommand parsedCommand = Parser.parse(ui.readCommand());
                isExit = execute(parsedCommand);
            } catch (HuhhhException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Dispatches and executes the given command (CLI path: prints via Ui).
     *
     * @param parsedCommand The command to execute.
     * @return true if the command is an exit command, false otherwise.
     * @throws HuhhhException If an error occurs during command execution.
     */
    private boolean execute(Parser.ParsedCommand parsedCommand) throws HuhhhException {
        switch (parsedCommand.getCommand()) {
        case LIST:
            ui.showTasks(tasks);
            return false;
        case MARK:
            handleMark(parsedCommand.getArguments());
            return false;
        case UNMARK:
            handleUnmark(parsedCommand.getArguments());
            return false;
        case DELETE:
            handleDelete(parsedCommand.getArguments());
            return false;
        case TODO:
            handleTodo(parsedCommand.getArguments());
            return false;
        case DEADLINE:
            handleDeadline(parsedCommand.getArguments());
            return false;
        case EVENT:
            handleEvent(parsedCommand.getArguments());
            return false;
        case FIND:
            handleFind(parsedCommand.getArguments());
            return false;
        case BYE:
            ui.showGoodbye();
            return true;
        default:
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
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
            return "Here are the matching tasks in your list:\n" + tasks.findTasks(keyword);
        }
        case BYE:
            isExit = true;
            return "Bye. Hope to see you again soon!";
        default:
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
    }

    private Task createTodo(String arguments) throws HuhhhException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new HuhhhException("Todo task must have a description.\nUsage: todo <desc>");
        }
        return new Todo(description);
    }

    private Task createDeadline(String arguments) throws HuhhhException {
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new HuhhhException("Deadline task must have a /by clause.\n"
                    + "Usage: deadline <desc> /by <date>");
        }
        String desc = arguments.substring(0, byIndex).trim();
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
        return new Deadline(desc, dueDate);
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
        String desc = arguments.substring(0, fromIndex).trim();
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
        return new Event(desc, from, to);
    }

    /**
     * Handles the 'find' command to find a task from a keyword,
     * and shows the list of tasks foudn.
     *
     * @param arguments The arguments containing the task index.
     * @throws HuhhhException If an error occurs during marking.
     */
    private void handleFind(String arguments) throws HuhhhException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new HuhhhException("Find command requires a keyword to search for.\nUsage: find <keyword>");
        }
        ui.showFoundTasks(tasks.findTasks(keyword));
    }

    /**
     * Handles the 'mark' command to mark a task as done, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task index.
     * @throws HuhhhException If an error occurs during marking.
     */
    private void handleMark(String arguments) throws HuhhhException {
        Task task = tasks.mark(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskMarked(task);
    }

    /**
     * Handles the 'unmark' command to mark a task as not done, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task index.
     * @throws HuhhhException If an error occurs during unmarking.
     */
    private void handleUnmark(String arguments) throws HuhhhException {
        Task task = tasks.unmark(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskUnmarked(task);
    }

    /**
     * Handles the 'delete' command to remove a task, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task index.
     * @throws HuhhhException If an error occurs during deletion.
     */
    private void handleDelete(String arguments) throws HuhhhException {
        Task task = tasks.delete(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskRemoved(task, tasks.size());
    }

    /**
     * Handles the 'todo' task command adds the Todo task, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task description.
     * @throws HuhhhException If an error occurs during addition.
     */
    private void handleTodo(String arguments) throws HuhhhException {
        addTask(createTodo(arguments));
    }

    /**
     * Handles the 'deadline' task command adds the Deadline task, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task description and due date.
     * @throws HuhhhException If an error occurs during addition.
     */
    private void handleDeadline(String arguments) throws HuhhhException {
        addTask(createDeadline(arguments));
    }

    /**
     * Handles the 'event' task command adds the Event task, updates storage,
     * and shows confirmation.
     *
     * @param arguments The arguments containing the task description and event dates.
     * @throws HuhhhException If an error occurs during addition.
     */
    private void handleEvent(String arguments) throws HuhhhException {
        addTask(createEvent(arguments));
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
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Persists the current task list to storage.
     *
     * @throws HuhhhException If an error occurs during saving.
     */
    private void persistTasks() throws HuhhhException {
        storage.save(tasks);
    }

    public static void main(String[] args) {
        Huhhh huhhh = args.length > 0 ? new Huhhh(args[0]) : new Huhhh();
        huhhh.run();
    }
}
