package business;

import db.Database;

import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpUtil;

import java.io.IOException;

import java.util.Map;

public class UserBusiness {

    private static final Logger logger = LoggerFactory.getLogger(UserBusiness.class);

    public HttpResponse signUp(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();
        String userId = body.get("userId");
        String name = body.get("name");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(userId, password, name, email);
        Database.addUser(user);

        return HttpResponse.redirect(httpRequest, "/index.html");
    }

    public HttpResponse login(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();

        User findUser = Database.findUserById(body.get("userId"));

        try {
            if (findUser == null) {
                return HttpResponse.loginFail(HttpStatus.OK, httpRequest);
            }

            if (!findUser.getPassword().equals(body.get("password"))) {
                return HttpResponse.loginFail(HttpStatus.OK, httpRequest);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        String sessionId = HttpUtil.generateRandomSessionId();
        Database.addUserSession(findUser, sessionId);
        return HttpResponse.redirectWithCookie(httpRequest, "/index.html", sessionId);
    }

}
