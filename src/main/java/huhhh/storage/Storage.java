package huhhh.storage;

import huhhh.HuhhhException;
import huhhh.task.Deadline;
import huhhh.task.Event;
import huhhh.task.Task;
import huhhh.task.TaskList;
import huhhh.task.Todo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving of tasks to a persistent storage file.
 */
public class Storage {
	private static final Path DEFAULT_PATH = Paths.get("data", "huhhh.txt");

	private final Path saveFile;

	public Storage() {
		this(DEFAULT_PATH);
	}

	public Storage(Path saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * Reads the storage file and deserializes its contents into a list of Task objects.
	 *
	 * @return A list of loaded Task objects.
	 * @throws HuhhhException If unable to read or parse the save file.
	 */
	public List<Task> load() throws HuhhhException {
		ensureFileExists();
		List<Task> loaded = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(saveFile)) {
				if (line.trim().isEmpty()) {
					continue;
				}
				loaded.add(parse(line));
			}
		} catch (IOException e) {
			throw new HuhhhException("Failed to read save file: " + e.getMessage());
		}
		return loaded;
	}

	/**
	 * Serializes and saves the given TaskList to the storage file.
	 *
	 * @param tasks The TaskList to save.
	 * @throws HuhhhException If unable to write to the save file.
	 */
	public void save(TaskList tasks) throws HuhhhException {
		ensureFileExists();
		try {
			Files.write(saveFile, tasks.serialisedList());
		} catch (IOException e) {
			throw new HuhhhException("Failed to write save file");
		}
	}

	/**
	 * Ensure that the save file and its parent directories exist.
	 * If they do not exist, create them.
	 *
	 * @throws HuhhhException If unable to create the file or directories.
	 */
	private void ensureFileExists() throws HuhhhException {
		try {
			Path parent = saveFile.getParent();
			if (parent != null && !Files.exists(parent)) {
				Files.createDirectories(parent);
			}
			if (!Files.exists(saveFile)) {
				Files.createFile(saveFile);
			}
		} catch (IOException e) {
			throw new HuhhhException("Unable to initialize save file");
		}
	}

	/**
	 * Parse a single line from storage into a Task object.
	 *
	 * @param line The raw storage line.
	 * @return The parsed Task object.
	 * @throws HuhhhException If the line is corrupted or invalid.
	 */
	private Task parse(String line) throws HuhhhException {
		String[] parts = line.split("\\|");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		if (parts.length < 3) {
			throw new HuhhhException("Corrupted save entry: " + line);
		}
		String type = parts[0];
		boolean isDone = parseDone(parts[1], line);
		String description = parts[2];
		Task task;
		switch (type) {
		case "T":
			task = new Todo(description);
			break;
		case "D":
			if (parts.length < 4) {
				throw new HuhhhException("Corrupted deadline entry: " + line);
			}
			LocalDate dueDate;
			try {
				dueDate = LocalDate.parse(parts[3]);
			} catch (DateTimeParseException e) {
				throw new HuhhhException("Corrupted deadline date: " + line);
			}
			task = new Deadline(description, dueDate);
			break;
		case "E":
			if (parts.length < 5) {
				throw new HuhhhException("Corrupted event entry: " + line);
			}
			task = new Event(description, parts[3], parts[4]);
			break;
		default:
			throw new HuhhhException("Unknown task type in save: " + type);
		}
		if (isDone) {
			task.markAsDone();
		}
		return task;
	}

	/**
	 * Parse the completion flag from storage line.
	 *
	 * @param value The raw completion flag value.
	 * @param rawLine The full raw storage line (for error reporting).
	 * @return True if the task is marked done, false otherwise.
	 * @throws HuhhhException
	 */
	private boolean parseDone(String value, String rawLine) throws HuhhhException {
		if ("1".equals(value)) {
			return true;
		}
		if ("0".equals(value)) {
			return false;
		}
		throw new HuhhhException("Invalid completion flag in entry: " + rawLine);
	}

}
