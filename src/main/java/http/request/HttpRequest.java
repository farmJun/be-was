package http.request;

import java.util.Map;

public class HttpRequest {

    public static class StartLine {
        private final String httpMethod;
        private final String path;
        private final String queryString;
        private final String httpVersion;

        public StartLine(String httpMethod, String path, String queryString, String httpVersion) {
            this.httpMethod = httpMethod;
            this.path = path;
            this.queryString = queryString;
            this.httpVersion = httpVersion;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public String getPath() {
            return path;
        }

        public String getHttpVersion() {
            return httpVersion;
        }

        public String getQueryString() {
            return queryString;
        }
    }

    private final StartLine startLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Map<String, String> body;
    private final boolean isStatic;

    public HttpRequest(StartLine startLine, Map<String, String> headers, Map<String, String> parameters, Map<String, String> body, boolean isStatic) {
        this.startLine = startLine;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public String getSessionId() {
        String cookieHeader = headers.get("Cookie");

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] tokens = cookie.trim().split("=", 2);
            if (tokens.length == 2 && tokens[0].equals("sid")) {
                return tokens[1];
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpRequest {\n");
        sb.append("\t").append(this.startLine.httpMethod).append(" ")
                .append(this.startLine.path).append(" ")
                .append(this.startLine.httpVersion).append("\n");

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
