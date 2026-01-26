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

public class Huhhh {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    public Huhhh() {
        this(new Storage());
    }

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

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (HuhhhException e) {
            ui.showLoadingError(e.getMessage());
            return new TaskList();
        }
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
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
        case BYE:
            ui.showGoodbye();
            return true;
        default:
            throw new HuhhhException("I'm sorry, but I don't know what that means :(");
        }
    }

    private void handleMark(String arguments) throws HuhhhException {
        Task task = tasks.mark(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskMarked(task);
    }

    private void handleUnmark(String arguments) throws HuhhhException {
        Task task = tasks.unmark(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskUnmarked(task);
    }

    private void handleDelete(String arguments) throws HuhhhException {
        Task task = tasks.delete(Parser.parseIndex(arguments));
        persistTasks();
        ui.showTaskRemoved(task, tasks.size());
    }

    private void handleTodo(String arguments) throws HuhhhException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new HuhhhException("Todo task must have a description.\nUsage: todo <desc>");
        }
        addTask(new Todo(description));
    }

    private void handleDeadline(String arguments) throws HuhhhException {
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new HuhhhException("Deadline task must have a /by clause.\nUsage: deadline <desc> /by <date>");
        }
        String desc = arguments.substring(0, byIndex).trim();
        if (desc.isEmpty()) {
            throw new HuhhhException("Deadline task must have a description.\nUsage: deadline <desc> /by <date>");
        }
        String by = arguments.substring(byIndex + 3).trim();
        if (by.isEmpty()) {
            throw new HuhhhException("Deadline task must have a specified /by date.\nUsage: deadline <desc> /by <date>");
        }
        LocalDate dueDate = Parser.parseDate(by);
        addTask(new Deadline(desc, dueDate));
    }

    private void handleEvent(String arguments) throws HuhhhException {
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
        addTask(new Event(desc, from, to));
    }

    private void addTask(Task task) throws HuhhhException {
        tasks.add(task);
        persistTasks();
        ui.showTaskAdded(task, tasks.size());
    }

    private void persistTasks() throws HuhhhException {
        storage.save(tasks);
    }

    public static void main(String[] args) {
        Huhhh huhhh = args.length > 0 ? new Huhhh(args[0]) : new Huhhh();
        huhhh.run();
    }
}