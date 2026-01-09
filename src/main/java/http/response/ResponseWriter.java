package http.response;

import business.Business;

import http.HttpStatus;
import http.request.RequestRouter;
import http.request.HttpRequest;

import util.DhtmlUtil;
import util.HttpUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

public class ResponseWriter {

    public static void write(OutputStream out, HttpRequest httpRequest) throws IOException {
        write(out, resolveResponse(httpRequest));
    }

    private static HttpResponse resolveResponse(HttpRequest request) throws IOException {
        if (request.isStatic()) {
            return handleStaticRequest(request);
        }
        return handleBusinessRequest(request);
    }

    private static HttpResponse handleStaticRequest(HttpRequest request) throws IOException {
        if (HttpUtil.isUnauthorized(request)) {
            return HttpResponse.redirect(request, "/login/index.html");
        }

        if (DhtmlUtil.supports(request.getStartLine().getPath())) {
            return DhtmlUtil.renderForLoginUser(request);
        }
        return HttpResponse.responseWithStaticRequest(HttpStatus.OK, request);
    }

    private static HttpResponse handleBusinessRequest(HttpRequest request) {
        Business business = RequestRouter.getHandler(request);
        return business.action(request);
    }

    private static void write(OutputStream out, HttpResponse response) throws IOException {
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
