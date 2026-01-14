package business;

import config.CommonConfig;

import db.Database;

import http.request.HttpRequest;
import http.request.MultiPartData;
import http.response.HttpResponse;

import model.Article;
import model.Image;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.UUID;

public class ArticleBusiness {
    private static final Logger logger = LoggerFactory.getLogger(ArticleBusiness.class);
    private static final String UPLOAD_DIR = CommonConfig.baseDirectory + "/asset";

    public HttpResponse createArticle(HttpRequest request) {
        try {
            MultiPartData data = request.getMultiPartData();
            String content = data.getParameter("content");
            Image uploadedFile = data.getFile("image");
            String savedFileName = null;

            User findUser = Database.findUserById(request.getSessionId());

            if (findUser == null) {
                return HttpResponse.notFound(request);
            }

            if (uploadedFile != null && uploadedFile.getFileName() != null && !uploadedFile.getFileName().isEmpty()) {
                String originalFilename = uploadedFile.getFileName();
                String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                savedFileName = UUID.randomUUID() + ext;

                File dir = new File(UPLOAD_DIR);
                File dest = new File(dir, savedFileName);
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(uploadedFile.getData());
                }
            }

            Article article = new Article(findUser.getUserId(), content, savedFileName);
            Database.addArticle(article);

            return HttpResponse.redirect(request, "/index.html");
        } catch (IOException e) {
            return HttpResponse.redirect(request, "/error/500.html");
        }
    }
}
