package business;

import db.Database;

import model.HttpRequest;
import model.HttpResponse;
import model.User;

import java.util.Map;

public class UserBusiness {

    public HttpResponse signUp(HttpRequest httpRequest) {
        Map<String, String> parameters = httpRequest.getParameters();
        String userId = parameters.get("userId");
        String name = parameters.get("name");
        String password = parameters.get("password");
        String email = parameters.get("email");

        User user = new User(userId, password, name, email);
        Database.addUser(user);

        return getHttpResponse(httpRequest);
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse(httpRequest.getHttpVersion());
        httpResponse.setStatus(200, "OK");
        return httpResponse;
    }
}
