package webserver;

import model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest getHttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String request = bufferedReader.readLine();
        String[] tokens = request.split(" ");
        String method = tokens[0];
        String fullPath = tokens[1];
        String path = fullPath.split("\\?", 2)[0];
        String httpVersion = tokens[2];

        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, String> parameters = new LinkedHashMap<>();
        String body = null;

        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            int idx = line.indexOf(":");
            String key = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(key, value);
        }

        boolean isStatic = path.matches(".+\\.[a-zA-Z0-9]+$");
        // 정적
        if (isStatic) {
            return new HttpRequest(method, path, httpVersion, headers, parameters, body, isStatic);
        }

        String[] pathParts = fullPath.split("\\?", 2);
        String queryString = pathParts.length > 1 ? pathParts[1] : "";
        parameters = parseQueryString(queryString);

        return new HttpRequest(method, path, httpVersion, headers, parameters, body, isStatic);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

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
