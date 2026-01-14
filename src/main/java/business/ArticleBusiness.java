package business;

import db.Database;

import http.request.HttpRequest;
import http.response.HttpResponse;

import model.Article;
import model.User;

public class ArticleBusiness {

    public HttpResponse createArticle(HttpRequest httpRequest) {
        String content = httpRequest.getForm().get("content");
        User user = Database.findUserBySessionId(httpRequest.getSessionId());

        if (user == null) {
            throw new IllegalStateException("유저 없음");
        }

        Article article = new Article(user.getUserId(), content);
        Database.addArticle(article);

        return HttpResponse.redirect(httpRequest, "/index.html");
    }
}
