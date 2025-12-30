package webserver;

import model.HttpResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

public class ResponseWriter {

    public static void write(OutputStream out, HttpResponse response) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.writeBytes(response.getHttpVersion() + " "
                + response.getStatusCode() + " "
                + response.getReasonPhrase() + "\r\n");

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            dataOutputStream.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        dataOutputStream.writeBytes("\r\n");

        if (response.getBody() != null) {
            dataOutputStream.write(response.getBody());
        }

        dataOutputStream.flush();
    }
}
