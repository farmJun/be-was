package business;

import db.Database;

import http.request.HttpRequest;
import http.response.HttpResponse;

import model.User;

import java.util.Map;

public class UserBusiness {

    public HttpResponse signUp(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();
        String userId = body.get("userId");
        String name = body.get("name");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(userId, password, name, email);
        Database.addUser(user);

        return HttpResponse.successWithRedirection(httpRequest, "/index.html");
    }

}
