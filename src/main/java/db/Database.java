package db;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String, User> sessions = new HashMap<>();

    public static void addUserSession(User user, String sessionId) {
        sessions.put(sessionId, user);
    }

    public static User findUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }
}
