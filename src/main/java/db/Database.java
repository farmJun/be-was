package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, User> sessions = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static void addUserSession(User user, String sessionId) {
        sessions.put(sessionId, user);
    }

    public static User findUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
