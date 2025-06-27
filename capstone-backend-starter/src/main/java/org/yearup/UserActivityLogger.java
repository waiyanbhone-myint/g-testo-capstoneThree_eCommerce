package org.yearup;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserActivityLogger {

    private static final String LOG_FILE = "user-activity.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logAction(String username, String action) {
        writeToFile(String.format("[%s] USER: %s | %s",
                LocalDateTime.now().format(formatter), username, action));
    }

    public static void logAction(String message, Object... params) {
        String formattedMessage = String.format(message, params);
        writeToFile(String.format("[%s] %s",
                LocalDateTime.now().format(formatter), formattedMessage));
    }

    public static void logError(String username, String error) {
        writeToFile(String.format("[%s] ERROR - USER: %s | %s",
                LocalDateTime.now().format(formatter), username, error));
    }

    public static void logSuccess(String username, String operation) {
        writeToFile(String.format("[%s] SUCCESS - USER: %s | %s",
                LocalDateTime.now().format(formatter), username, operation));
    }

    public static void logAdmin(String username, String action) {
        writeToFile(String.format("[%s] ADMIN: %s | %s",
                LocalDateTime.now().format(formatter), username, action));
    }

    public static void logSecurity(String event) {
        writeToFile(String.format("[%s] SECURITY | %s",
                LocalDateTime.now().format(formatter), event));
    }

    private static void writeToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}