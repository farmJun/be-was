package http.request;

import model.Image;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

public class MultiPartParser {

    public static MultiPartData parse(InputStream in, String contentType, int contentLength) throws IOException {
        MultiPartData data = new MultiPartData();
        String boundaryStr = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
        byte[] boundary = boundaryStr.getBytes(StandardCharsets.UTF_8);
        byte[] bodyBytes = in.readNBytes(contentLength);
        List<byte[]> parts = splitByBoundary(bodyBytes, boundary);

        for (byte[] part : parts) {
            parsePart(part, data);
        }

        return data;
    }

    private static List<byte[]> splitByBoundary(byte[] body, byte[] boundary) {
        List<byte[]> parts = new ArrayList<>();
        int start = 0;

        for (int i = 0; i < body.length - boundary.length; i++) {
            boolean match = true;
            for (int j = 0; j < boundary.length; j++) {
                if (body[i + j] != boundary[j]) {
                    match = false;
                    break;
                }
            }

            if (match) {
                if (start > 0) {
                    int end = i - 2;
                    if (end > start) {
                        byte[] part = new byte[end - start];
                        System.arraycopy(body, start, part, 0, part.length);
                        parts.add(part);
                    }
                }
                start = i + boundary.length + 2;
                i = start - 1;
            }
        }
        return parts;
    }

    private static void parsePart(byte[] part, MultiPartData data) {
        int headerEndIndex = -1;
        for (int i = 0; i < part.length - 3; i++) {
            if (part[i] == '\r' && part[i+1] == '\n' && part[i+2] == '\r' && part[i+3] == '\n') {
                headerEndIndex = i;
                break;
            }
        }

        if (headerEndIndex == -1){
            return;
        }

        String headerString = new String(part, 0, headerEndIndex, StandardCharsets.UTF_8);
        String name = extractHeaderValue(headerString, "name=\"");
        String filename = extractHeaderValue(headerString, "filename=\"");

        int bodyStart = headerEndIndex + 4;
        int bodyLength = part.length - bodyStart;
        byte[] content = new byte[bodyLength];
        System.arraycopy(part, bodyStart, content, 0, bodyLength);

        if (filename != null) {
            String partContentType = "application/octet-stream";
            data.addFile(name, new Image(filename, partContentType, content));
        } else {
            String value = new String(content, StandardCharsets.UTF_8);
            data.addFormField(name, value);
        }
    }

    private static String extractHeaderValue(String header, String key) {
        int start = header.indexOf(key);
        if (start == -1) {
            return null;
        }
        start += key.length();
        int end = header.indexOf("\"", start);
        return header.substring(start, end);
    }
}