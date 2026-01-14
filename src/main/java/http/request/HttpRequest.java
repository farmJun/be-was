package http.request;

import model.Image;

import java.util.Map;

public class HttpRequest {

    public record StartLine(String httpMethod, String path, String queryString, String httpVersion) {
    }

    private final StartLine startLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Map<String, String> form;
    private final Image image;

    public HttpRequest(StartLine startLine, Map<String, String> headers, Map<String, String> parameters, Map<String, String> form, Image image) {
        this.startLine = startLine;
        this.headers = headers;
        this.parameters = parameters;
        this.form = form;
        this.image = image;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getForm() {
        return form;
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

        if (form != null) {
            sb.append("\tBody:\n");
            sb.append("\t\t").append(form).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }

}
