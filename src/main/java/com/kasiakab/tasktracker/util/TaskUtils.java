package com.kasiakab.tasktracker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Utility class for common operations.
 */
public class TaskUtils {

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_CATEGORY_NAME_LENGTH = 50;

    public static final String DEFAULT_STATUS = "TODO";
    public static final String DEFAULT_PRIORITY = "MEDIUM";
    public static final String DEFAULT_COLOR = "#808080";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public TaskUtils() {
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dateTime.format(formatter);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidTaskTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        if (title.length() > 100) {
            return false;
        }
        return true;
    }


    public static boolean isValidTaskDescription(String description) {
        if (description == null) {
            return true;
        }
        if (description.length() > 500) {
            return false;
        }
        return true;
    }

    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    public static String generateSlug(String title) {
        if (title == null) {
            return "";
        }
        String slug = title.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-");
        slug = slug.replaceAll("^-|-$", "");
        return slug;
    }
}
