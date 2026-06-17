package com.fooddelivery.web;

import com.fooddelivery.model.User;
import jakarta.servlet.http.HttpSession;

/**
 * Helper utilities for managing the logged-in user in the HTTP session.
 * Stores the authenticated User under "currentUser" and a friendly flash
 * message under "flash".
 */
public final class SessionUtil {

    public static final String CURRENT_USER = "currentUser";
    public static final String FLASH = "flash";
    public static final String FLASH_ERROR = "flashError";

    private SessionUtil() {}

    public static User getCurrentUser(HttpSession session) {
        Object o = session.getAttribute(CURRENT_USER);
        return o instanceof User ? (User) o : null;
    }

    public static void setCurrentUser(HttpSession session, User user) {
        session.setAttribute(CURRENT_USER, user);
    }

    public static void clearCurrentUser(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public static void flash(HttpSession session, String message) {
        session.setAttribute(FLASH, message);
    }

    public static void flashError(HttpSession session, String message) {
        session.setAttribute(FLASH_ERROR, message);
    }
}
