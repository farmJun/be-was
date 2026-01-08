package util;

import db.Database;
import http.request.HttpRequest;
import model.User;

import java.security.SecureRandom;
import java.util.Base64;

public class HttpUtil {

    public static String generateRandomSessionId() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static boolean isLoggedIn(HttpRequest httpRequest) {
        User user = Database.findUserBySessionId(httpRequest.getSessionId());

        return user != null;
    }
}
