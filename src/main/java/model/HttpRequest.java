package model;

import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;
    private final boolean isStatic;

    public HttpRequest(String httpMethod, String path, String httpVersion, Map<String, String> headers, Map<String, String> parameters, String body, boolean isStatic) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
        this.isStatic = isStatic;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpRequest {\n");
        sb.append("\t").append(httpMethod).append(" ")
                .append(path).append(" ")
                .append(httpVersion).append("\n");

        if (parameters != null && !parameters.isEmpty()) {
            sb.append("\tParameters:\n");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sb.append("\t\t")
                        .append(entry.getKey())
                        .append(" = ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }

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
