package http.response;

import config.CommonConfig;

import http.HttpStatus;
import http.request.HttpRequest;

import util.HttpUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

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

    public static HttpResponse successWithStaticRequest(HttpRequest httpRequest) throws IOException {
        byte[] body = Files.readAllBytes(new File(CommonConfig.baseDirectory + httpRequest.getStartLine().getPath()).toPath());
        HttpResponse httpResponse = new HttpResponse(httpRequest.getStartLine().getHttpVersion());
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.addHeader("Content-Type", HttpUtil.getContentType(httpRequest.getStartLine().getPath()));
        httpResponse.setBody(body);
        return httpResponse;
    }

    public static HttpResponse successWithRedirection(HttpRequest httpRequest, String redirection) {
        HttpResponse httpResponse = new HttpResponse(httpRequest.getStartLine().getHttpVersion());
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.addHeader("Location", redirection);
        httpResponse.addHeader("Set-Cookie", "sid=1231234, path=/");
        return httpResponse;
    }
}
