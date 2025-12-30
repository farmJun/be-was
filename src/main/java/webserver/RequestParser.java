package webserver;

import model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest getHttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String request = bufferedReader.readLine();
        String[] tokens = request.split(" ");
        String method = tokens[0];
        String path = tokens[1];
        String httpVersion = tokens[2];

        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            int idx = line.indexOf(":");
            String key = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(key, value);
        }

        String body = null;
        return new HttpRequest(method, path, httpVersion, headers, body);
    }
}
