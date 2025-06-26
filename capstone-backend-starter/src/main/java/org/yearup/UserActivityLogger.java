package org.yearup;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserActivityLogger {

    private static final String LOG_FILE = "user-activity.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Log user actions
    public static void logAction(String message) {
        writeToFile(message);
    }

    // Log with formatted string
    public static void logAction(String message, Object... params) {
        writeToFile(String.format(message, params));
    }

    // Simple method to write to file
    private static void writeToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = String.format("[%s] %s%n", timestamp, message);
            writer.write(logEntry);
            writer.flush();
        } catch (IOException e) {
            // Silently ignore file errors
        }
    }
}