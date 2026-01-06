package business;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface Business {

    HttpResponse action(HttpRequest httpRequest);
}
