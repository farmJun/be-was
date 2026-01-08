package http;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JS("application/javascript", ".js"),
    PNG("image/png", ".png"),
    JPG("image/jpeg", ".jpg", ".jpeg"),
    SVG("image/svg+xml", ".svg"),
    ICO("image/vnd.microsoft.icon", ".ico"),
    OCTET_STREAM("application/octet-stream");

    private final String value;
    private final String[] extensions;

    ContentType(String value, String... extensions) {
        this.value = value;
        this.extensions = extensions;
    }

    public String getValue() {
        return value;
    }

    public static ContentType from(String path) {
        return Arrays.stream(values())
                .filter(type -> type.matches(path))
                .findFirst()
                .orElse(OCTET_STREAM);
    }

    private boolean matches(String path) {
        return Arrays.stream(extensions)
                .anyMatch(path::endsWith);
    }
}
