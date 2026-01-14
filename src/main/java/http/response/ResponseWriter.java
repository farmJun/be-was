package http.response;

import http.HttpStatus;
import http.request.RequestRouter;
import http.request.HttpRequest;

import model.RouteKey;
import model.RouteResult;

import util.DhtmlUtil;
import util.HttpUtil;
import util.StaticFileResolver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

public class ResponseWriter {

    public static void write(OutputStream out, HttpRequest httpRequest) throws IOException {
        write(out, resolveResponse(httpRequest));
    }

    private static HttpResponse resolveResponse(HttpRequest request) throws IOException {
        RouteKey routeKey = new RouteKey(request);
        RouteResult routeResult = RequestRouter.route(routeKey);

        if (routeResult.hasHandler()) {
            return routeResult.getHandler().action(request);
        }

        if (routeResult.isMethodNotAllowed()) {
            return HttpResponse.methodNotAllowed(request, routeResult.getAllowedMethods());
        }

        if (!StaticFileResolver.exists(routeKey.getPath())) {
            return HttpResponse.notFound(request);
        }

        return handleStaticRequest(request);
    }

    private static HttpResponse handleStaticRequest(HttpRequest request) throws IOException {
        if (HttpUtil.isUnauthorized(request)) {
            return HttpResponse.redirect(request, "/login/index.html");
        }

        if (DhtmlUtil.supports(request.getStartLine().path())) {
            return DhtmlUtil.renderForLoginUser(request);
        }

        return HttpResponse.responseWithStaticRequest(HttpStatus.OK, request);
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
