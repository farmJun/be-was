package util;

import db.Database;

import http.request.HttpRequest;

import model.User;

import java.security.SecureRandom;

import java.util.Base64;
import java.util.Set;

public class HttpUtil {

    private static final Set<String> LOGIN_REQUIRED_PATHS = Set.of(
            "/mypage/index.html",
            "/article/index.html"
    );

    public static String generateRandomSessionId() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static boolean isUnauthorized(HttpRequest request) {
        return isLoginRequiredPath(request.getStartLine().path()) && !isLoggedIn(request);
    }

    private static boolean isLoginRequiredPath(String path) {
        return LOGIN_REQUIRED_PATHS.contains(path);
    }

    public static boolean isLoggedIn(HttpRequest httpRequest) {
        User user = Database.findUserBySessionId(httpRequest.getSessionId());

        return user != null;
    }
}
