package webserver;

import java.io.*;

import java.net.Socket;

import java.nio.file.Files;

import business.Business;

import model.HttpRequest;
import model.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final String baseDirectory = "/Users/apple/be-was/src/main/resources/static/";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            // 웹 서버 1단계 - HTTP Request 내용 출력
            HttpRequest httpRequest = RequestParser.getHttpRequest(in);
            logger.info(httpRequest.toString());

            if (httpRequest.isStatic()) {
                // 웹 서버 1단계 - index.html 응답
                HttpResponse httpResponse = getHttpResponse(httpRequest);
                ResponseWriter.write(out, httpResponse);
                return;
            }

            Business business = RequestRouter.getHandler(httpRequest);
            HttpResponse httpResponse = business.action(httpRequest);
            ResponseWriter.write(out, httpResponse);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest) throws IOException {
        byte[] body = Files.readAllBytes(new File(baseDirectory + httpRequest.getPath()).toPath());
        HttpResponse httpResponse = new HttpResponse(httpRequest.getHttpVersion());
        httpResponse.setStatus(200, "OK");
        httpResponse.addHeader("Content-Type", getContentType(httpRequest.getPath()));
        httpResponse.setBody(body);
        return httpResponse;
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        } else if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (path.endsWith(".ico")) {
            return "image/vnd.microsoft.icon";
        }
        return "application/octet-stream";
    }

}
