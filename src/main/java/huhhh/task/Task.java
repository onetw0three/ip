package huhhh.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import huhhh.HuhhhException;

/**
 * Represents a general task with a description and completion status.
 * To be used as a base class for specific task types.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Constructs a Task with the given description.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns true if the task is marked done.
     */
    protected boolean isDone() {
        return isDone;
    }

    /**
     * Returns true if {@code keyword} is contained in the task description.
     *
     * @param keyword String to search for in the description.
     * @return true if the keyword is found in the description.
     */
    public boolean containsKeyword(String keyword) {
        if (keyword == null) {
            return false;
        }
        return description.contains(keyword);
    }

    /**
     * Returns true if this task has the given tag.
     * Tag comparisons are case-insensitive and the leading '#' is optional.
     */
    public boolean hasTag(String rawTag) {
        String normalized = normalizeStorageTag(rawTag);
        return normalized != null && tags.contains(normalized);
    }

    /**
     * Adds a tag to this task. Duplicate tags are ignored.
     *
     * @param rawTag Tag token like "#fun".
     * @throws HuhhhException if the tag is invalid.
     */
    public void addTag(String rawTag) throws HuhhhException {
        String normalized = normalizeTagStrict(rawTag);
        tags.add(normalized);
    }

    /**
     * Removes a tag from this task.
     *
     * @param rawTag Tag token like "#fun".
     * @throws HuhhhException if the tag is invalid.
     */
    public void removeTag(String rawTag) throws HuhhhException {
        String normalized = normalizeTagStrict(rawTag);
        tags.remove(normalized);
    }

    /**
     * Returns an unmodifiable view of the tags.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Sets tags from storage (comma-separated, without '#'). Invalid tokens are ignored.
     */
    public void loadTagsFromStorageField(String storageField) {
        if (storageField == null) {
            return;
        }
        String trimmed = storageField.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        for (String token : trimmed.split(",")) {
            String normalized = normalizeStorageTag(token);
            if (normalized != null) {
                tags.add(normalized);
            }
        }
    }

    /**
     * Serialises tags for storage as a comma-separated list without leading '#'.
     * Returns empty string when there are no tags.
     */
    protected String serialisedTagsField() {
        if (tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }

    /**
     * Extract tags from a free-form argument string.
     * Tags are tokens that start with '#'. Returns description with tag tokens removed.
     */
    public static ParsedTextWithTags parseDescriptionAndTags(String raw) {
        if (raw == null) {
            return new ParsedTextWithTags("", Set.of());
        }

        String[] tokens = raw.trim().split("\\s+");
        if (tokens.length == 1 && tokens[0].isEmpty()) {
            return new ParsedTextWithTags("", Set.of());
        }

        Set<String> tags = Arrays.stream(tokens)
                .filter(t -> t.startsWith("#"))
                .map(Task::normalizeTag)
                .filter(t -> t != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String description = Arrays.stream(tokens)
                .filter(t -> !t.startsWith("#"))
                .collect(Collectors.joining(" "))
                .trim();

        return new ParsedTextWithTags(description, tags);
    }

    /**
     * Returns the status icon representing whether the task is done.
     *
     * @return "X" if the task is done, otherwise a space " ".
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        String base = String.format("[%s] %s", getStatusIcon(), description);
        if (tags.isEmpty()) {
            return base;
        }
        String renderedTags = tags.stream()
                .map(t -> "#" + t)
                .collect(Collectors.joining(" "));
        return base + " (" + renderedTags + ")";
    }

    /**
     * Normalizes a tag token. Returns null for invalid tags.
     * Expected user-facing form is "#tag"; the storage form omits '#'.
     */
    private static String normalizeTag(String rawTag) {
        if (rawTag == null) {
            return null;
        }
        String t = rawTag.trim();
        if (t.isEmpty()) {
            return null;
        }

        boolean hadHash = t.startsWith("#");
        if (hadHash) {
            t = t.substring(1);
        }

        t = t.trim().toLowerCase();
        if (t.isEmpty()) {
            return null;
        }
        // allow letters/digits/_/- only
        if (!t.matches("[a-z0-9_-]+")) {
            return null;
        }

        // If user input didn't include '#', treat it as invalid.
        // (Storage loading uses loadTagsFromStorageField which bypasses this via explicit normalization there.)
        return hadHash ? t : null;
    }

    private static String normalizeTagStrict(String rawTag) throws HuhhhException {
        String normalized = normalizeTag(rawTag);
        if (normalized == null) {
            throw new HuhhhException("Invalid tag: " + rawTag);
        }
        return normalized;
    }

    private static String normalizeStorageTag(String rawTag) {
        if (rawTag == null) {
            return null;
        }
        String t = rawTag.trim().toLowerCase();
        if (t.startsWith("#")) {
            t = t.substring(1);
        }
        if (t.isEmpty()) {
            return null;
        }
        if (!t.matches("[a-z0-9_-]+")) {
            return null;
        }
        return t;
    }

    /**
     * Serialises the task into a string format suitable for storage.
     *
     * @return The serialised string representation of the task.
     */
    public abstract String serialisedString();

    /**
     * Simple holder for parsed description + tags.
     */
    public static class ParsedTextWithTags {
        private final String text;
        private final Set<String> tags;

        /**
         * Constructs a ParsedTextWithTags instance.
         *
         * @param text The description text without tags.
         * @param tags The set of extracted tags.
         */
        public ParsedTextWithTags(String text, Set<String> tags) {
            this.text = text;
            this.tags = tags;
        }

        public String getText() {
            return text;
        }

        public Set<String> getTags() {
            return tags;
        }
    }
}
