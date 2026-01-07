package http.response;

import config.CommonConfig;

import http.HttpStatus;
import http.request.HttpRequest;

import util.HttpUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public static HttpResponse responseWithStaticRequest(HttpStatus httpStatus, HttpRequest httpRequest) throws IOException {
        byte[] body = Files.readAllBytes(new File(CommonConfig.baseDirectory + httpRequest.getStartLine().getPath()).toPath());
        HttpResponse httpResponse = new HttpResponse(httpRequest.getStartLine().getHttpVersion());
        httpResponse.setStatus(httpStatus);
        httpResponse.addHeader("Content-Type", HttpUtil.getContentType(httpRequest.getStartLine().getPath()));
        httpResponse.setBody(body);
        return httpResponse;
    }

    public static HttpResponse loginFail(HttpStatus httpStatus, HttpRequest httpRequest, String viewPath) throws IOException {
        String html = Files.readString(
                Path.of(CommonConfig.baseDirectory + "/login/index.html")
        );

        html = html.replace(
                "class=\"overlay hidden\"",
                "class=\"overlay\""
        );

        HttpResponse httpResponse = new HttpResponse(httpRequest.getStartLine().getHttpVersion());
        httpResponse.setStatus(httpStatus);
        httpResponse.addHeader("Content-Type", HttpUtil.getContentType(viewPath));
        httpResponse.setBody(html.getBytes(StandardCharsets.UTF_8));
        return httpResponse;
    }

    public static HttpResponse responseWithRedirection(HttpRequest httpRequest, String redirection) {
        HttpResponse httpResponse = new HttpResponse(httpRequest.getStartLine().getHttpVersion());
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.addHeader("Location", redirection);
        return httpResponse;
    }

    public static HttpResponse responseWithRedirectionAndCookie(HttpRequest httpRequest, String redirection, String sessionId) {
        HttpResponse httpResponse = responseWithRedirection(httpRequest, redirection);
        httpResponse.addHeader("Set-Cookie", String.format("sid=%s; path=/;", sessionId));
        return httpResponse;
    }
}
