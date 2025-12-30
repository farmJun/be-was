package model;

import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String httpMethod, String path, String httpVersion, Map<String, String> headers, String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpRequest {\n");
        sb.append("\t").append(httpMethod).append(" ")
                .append(path).append(" ")
                .append(httpVersion).append("\n");

        sb.append("\tHeaders:\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("\t\t")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        if (body != null) {
            sb.append("\tBody:\n");
            sb.append("\t\t").append(body).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }

}
