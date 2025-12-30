package model;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final String httpVersion;
    private int statusCode;
    private String reasonPhrase;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body;

    public HttpResponse(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
        addHeader("Content-Length", String.valueOf(body.length));
    }

    public byte[] getBody() {
        return body;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
