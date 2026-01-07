package http.request;

import java.io.*;

import java.net.Socket;

import db.Database;

import http.response.ResponseWriter;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = RequestParser.getHttpRequest(in);
            User user = Database.findUserBySessionId(httpRequest.getSessionId());
            logger.info(httpRequest.toString());

            if(user != null){
                logger.info("발견" + user);
            }

            ResponseWriter.write(out, httpRequest);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
