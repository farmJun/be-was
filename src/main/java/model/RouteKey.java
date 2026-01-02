package model;

import java.util.Objects;

public class RouteKey {
    private final String method;
    private final String path;

    public RouteKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteKey)) return false;
        RouteKey that = (RouteKey) o;
        return method.equals(that.method) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
