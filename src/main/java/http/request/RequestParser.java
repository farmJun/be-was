package http.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest getHttpRequest(InputStream in) throws IOException {
        String requestStartLine = readLine(in);
        HttpRequest.StartLine startLine = parseStartLine(requestStartLine);

        Map<String, String> headers = parseHeader(in);
        Map<String, String> parameters = parseQueryParameter(startLine);

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        String requestContentType = headers.getOrDefault("Content-Type", "");

        Map<String, String> form = new LinkedHashMap<>();
        MultiPartData multiPartData = new MultiPartData();

        if (startLine.httpMethod().equalsIgnoreCase("POST") && contentLength > 0) {
            if (requestContentType.startsWith("application/x-www-form-urlencoded")) {
                form = parseUrlEncodedBody(in, contentLength);
            } else if (requestContentType.startsWith("multipart/form-data")) {
                multiPartData = MultiPartParser.parse(in, requestContentType, contentLength);
            }
        }

        return new HttpRequest(startLine, headers, parameters, form, multiPartData);
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\r') {
                int next = in.read();
                if (next == '\n') {
                    break;
                }
                buffer.write(b);
                buffer.write(next);
            } else {
                buffer.write(b);
            }
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    private static HttpRequest.StartLine parseStartLine(String startLine) {
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid Request");
        }
        String[] tokens = startLine.split(" ");
        String method = tokens[0];
        String fullPath = tokens[1];
        String httpVersion = tokens[2];

        String[] pathParts = fullPath.split("\\?", 2);
        String path = pathParts[0];
        String queryString = pathParts.length > 1 ? pathParts[1] : "";

        return new HttpRequest.StartLine(method, path, queryString, httpVersion);
    }

    private static Map<String, String> parseHeader(InputStream in) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while (!(line = readLine(in)).isEmpty()) {
            int idx = line.indexOf(":");
            if (idx != -1) {
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static Map<String, String> parseQueryParameter(HttpRequest.StartLine startLine) {
        if (startLine.httpMethod().equalsIgnoreCase("GET")) {
            return parseStringToMap(startLine.queryString());
        }
        return new LinkedHashMap<>();
    }

    private static Map<String, String> parseUrlEncodedBody(InputStream in, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return new LinkedHashMap<>();
        }

        byte[] bodyBytes = in.readNBytes(contentLength);
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        return parseStringToMap(bodyString);
    }

    private static Map<String, String> parseStringToMap(String queryString) {
        Map<String, String> parameters = new LinkedHashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return parameters;
        }

        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";
            parameters.put(key, value);
        }
        return parameters;
    }
}