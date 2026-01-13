package model;

import business.Business;

import java.util.Set;

public class RouteResult {

    private final Business handler;
    private final boolean pathExists;
    private final Set<String> allowedMethods;

    public RouteResult(Business handler, boolean pathExists, Set<String> allowedMethods) {
        this.handler = handler;
        this.pathExists = pathExists;
        this.allowedMethods = allowedMethods;
    }

    public boolean hasHandler() {
        return handler != null;
    }

    public boolean isMethodNotAllowed() {
        return handler == null && pathExists;
    }

    public Business getHandler() {
        return handler;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }
}
