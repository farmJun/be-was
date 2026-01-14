package http.response;

import config.CommonConfig;

import http.ContentType;
import http.HttpStatus;
import http.request.HttpRequest;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private final String httpVersion;
    private HttpStatus httpStatus;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body;

    public HttpResponse(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static Builder builder(HttpRequest request) {
        return new Builder(request.getStartLine().httpVersion());
    }

    public static HttpResponse responseWithStaticRequest(HttpStatus status, HttpRequest request) throws IOException {
        String path = request.getStartLine().path();
        byte[] body = Files.readAllBytes(
                Path.of(CommonConfig.baseDirectory + path)
        );

        return HttpResponse.builder(request)
                .status(status)
                .contentType(ContentType.from(path))
                .body(body)
                .build();
    }

    public static HttpResponse notFound(HttpRequest request) throws IOException {
        byte[] body = Files.readAllBytes(
                Path.of(CommonConfig.baseDirectory + "/error/404.html")
        );

        return HttpResponse.builder(request)
                .status(HttpStatus.NOT_FOUND)
                .contentType(ContentType.HTML)
                .body(body)
                .build();
    }

    public static HttpResponse methodNotAllowed(HttpRequest request, Set<String> allowedMethods) throws IOException {

        byte[] body = Files.readAllBytes(
                Path.of(CommonConfig.baseDirectory + "/error/405.html")
        );

        String allowHeader = String.join(", ", allowedMethods);

        return HttpResponse.builder(request)
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Allow", allowHeader)
                .contentType(ContentType.HTML)
                .body(body)
                .build();
    }

    public static HttpResponse loginFail(HttpStatus status, HttpRequest request) throws IOException {

        String html = Files.readString(
                Path.of(CommonConfig.baseDirectory + "/login/index.html")
        ).replace(
                "class=\"overlay hidden\"",
                "class=\"overlay\""
        );

        return HttpResponse.builder(request)
                .status(status)
                .contentType(ContentType.HTML)
                .body(html.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public static HttpResponse redirect(HttpRequest request, String location) {
        return HttpResponse.builder(request)
                .status(HttpStatus.FOUND)
                .header("Location", location)
                .build();
    }

    public static HttpResponse redirectWithCookie(HttpRequest request, String location, String sessionId) {
        return HttpResponse.builder(request)
                .status(HttpStatus.FOUND)
                .header("Location", location)
                .header("Set-Cookie", "sid=" + sessionId + "; path=/;")
                .build();
    }

    public static class Builder {

        private final HttpResponse response;

        private Builder(String httpVersion) {
            this.response = new HttpResponse(httpVersion);
        }

        public Builder status(HttpStatus status) {
            response.setStatus(status);
            return this;
        }

        public Builder header(String key, String value) {
            response.addHeader(key, value);
            return this;
        }

        public Builder contentType(ContentType type) {
            response.addHeader("Content-Type", type.getValue());
            return this;
        }

        public Builder body(byte[] body) {
            response.setBody(body);
            return this;
        }

        public HttpResponse build() {
            return response;
        }
    }

}
