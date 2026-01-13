package http.request;

import business.Business;
import business.ArticleBusiness;
import business.UserBusiness;

import model.RouteKey;
import model.RouteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestRouter {

    private static final Map<String, Map<String, Business>> mappings = new HashMap<>();

    static {
        UserBusiness userBusiness = new UserBusiness();
        ArticleBusiness articleBusiness = new ArticleBusiness();
        register("/user/create", "POST", userBusiness::signUp);
        register("/user/login", "POST", userBusiness::login);
        register("/article/create", "POST", articleBusiness::createArticle);
    }

    public static RouteResult route(RouteKey routeKey) {
        Map<String, Business> methodMap = mappings.get(routeKey.getPath());

        if (methodMap == null) {
            return new RouteResult(null, false, Set.of());
        }

        Business handler = methodMap.get(routeKey.getMethod());

        if (handler == null) {
            return new RouteResult(null, true, Set.of());
        }

        return new RouteResult(handler, true, methodMap.keySet());
    }

    private static void register(String path, String httpMethod, Business business) {
        mappings.computeIfAbsent(path, p -> new HashMap<>())
                .put(httpMethod, business);
    }

}
