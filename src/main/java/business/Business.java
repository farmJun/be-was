package business;

import model.HttpRequest;
import model.HttpResponse;

public interface Business {

    HttpResponse action(HttpRequest httpRequest);
}
