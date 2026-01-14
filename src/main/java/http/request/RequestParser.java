package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest getHttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String requestStartLine = bufferedReader.readLine();
        HttpRequest.StartLine startLine = parseStartLine(requestStartLine);
        Map<String, String> headers = parseHeader(bufferedReader);
        Map<String, String> parameters = parseQueryParameter(startLine);
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        Map<String, String> body = parseBody(bufferedReader, startLine, contentLength);
        return new HttpRequest(startLine, headers, parameters, body);
    }

    private static HttpRequest.StartLine parseStartLine(String startLine) {
        String[] tokens = startLine.split(" ");
        String method = tokens[0];
        String fullPath = tokens[1];
        String httpVersion = tokens[2];

        String[] pathParts = fullPath.split("\\?", 2);
        String path = pathParts[0];
        String queryString = pathParts.length > 1 ? pathParts[1] : "";

        return new HttpRequest.StartLine(method, path, queryString, httpVersion);
    }

    private static Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();

        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            int idx = line.indexOf(":");
            String key = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(key, value);
        }

        return headers;
    }

    private static Map<String, String> parseQueryParameter(HttpRequest.StartLine startLine) {
        if (startLine.httpMethod().equalsIgnoreCase("GET")) {
            return parseStringToMap(startLine.queryString());
        }
        return new LinkedHashMap<>();
    }

    private static Map<String, String> parseBody(BufferedReader bufferedReader, HttpRequest.StartLine startLine, int contentLength) throws IOException {
        if (startLine.httpMethod().equalsIgnoreCase("POST")) {
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                bufferedReader.read(bodyChars, 0, contentLength);
                String bodyString = new String(bodyChars);
                return parseStringToMap(bodyString);
            }
        }
        return new LinkedHashMap<>();
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
