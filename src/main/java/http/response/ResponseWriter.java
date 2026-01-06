package http.response;

import business.Business;

import http.request.RequestRouter;
import http.request.HttpRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

public class ResponseWriter {

    public static void write(OutputStream out, HttpRequest httpRequest) throws IOException {
        if (httpRequest.isStatic()) {
            HttpResponse httpResponse = HttpResponse.successWithStaticRequest(httpRequest);
            write(out, httpResponse);
            return;
        }

        Business business = RequestRouter.getHandler(httpRequest);
        HttpResponse httpResponse = business.action(httpRequest);
        write(out, httpResponse);
    }

    public static void write(OutputStream out, HttpResponse response) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        writeResponseStartLine(dataOutputStream, response);
        writeResponseHeader(dataOutputStream, response);
        writeResponseBody(response, dataOutputStream);
    }

    private static void writeResponseStartLine(DataOutputStream dataOutputStream, HttpResponse response) throws IOException {
        dataOutputStream.writeBytes(response.getHttpVersion() + " "
                + response.getHttpStatus().getStatusCode() + " "
                + response.getHttpStatus().getMessage() + "\r\n");
    }

    private static void writeResponseHeader(DataOutputStream dataOutputStream, HttpResponse response) throws IOException {
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            dataOutputStream.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        dataOutputStream.writeBytes("\r\n");
    }

    private static void writeResponseBody(HttpResponse response, DataOutputStream dataOutputStream) throws IOException {
        if (response.getBody() != null) {
            dataOutputStream.write(response.getBody());
        }

        dataOutputStream.flush();
    }
}
