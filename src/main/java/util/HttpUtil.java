package util;

import java.security.SecureRandom;
import java.util.Base64;

public class HttpUtil {
    public static String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        } else if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (path.endsWith(".ico")) {
            return "image/vnd.microsoft.icon";
        }
        return "application/octet-stream";
    }

    public static String generateRandomSessionId() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}
