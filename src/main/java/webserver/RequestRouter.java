package webserver;

import business.Business;
import business.UserBusiness;

import model.HttpRequest;
import model.RouteKey;

import java.util.HashMap;
import java.util.Map;

public class RequestRouter {

    private static final Map<RouteKey, Business> mappings = new HashMap<>();

    static {
        UserBusiness userBusiness = new UserBusiness();

        mappings.put(
                new RouteKey("GET", "/create"),
                userBusiness::signUp
        );
    }

    public static Business getHandler(HttpRequest request) {
        return mappings.get(new RouteKey(request.getHttpMethod(), request.getPath()));
    }

}
