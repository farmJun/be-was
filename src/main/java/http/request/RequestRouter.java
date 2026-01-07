package http.request;

import business.Business;
import business.UserBusiness;

import model.RouteKey;

import java.util.HashMap;
import java.util.Map;

public class RequestRouter {

    private static final Map<RouteKey, Business> mappings = new HashMap<>();

    static {
        UserBusiness userBusiness = new UserBusiness();

        mappings.put(
                new RouteKey("POST", "/user/create"),
                userBusiness::signUp
        );

        mappings.put(
                new RouteKey("POST", "/user/login"),
                userBusiness::login
        );
    }

    public static Business getHandler(HttpRequest request) {
        return mappings.get(new RouteKey(request.getStartLine().getHttpMethod(), request.getStartLine().getPath()));
    }

}
