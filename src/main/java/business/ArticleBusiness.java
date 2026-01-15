package business;

import config.CommonConfig;

import db.Database;

import http.ContentType;
import http.HttpStatus;
import http.request.HttpRequest;
import http.request.MultiPartData;
import http.response.HttpResponse;

import model.Article;
import model.Image;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DhtmlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ArticleBusiness {
    private static final Logger logger = LoggerFactory.getLogger(ArticleBusiness.class);

    public HttpResponse createArticle(HttpRequest request) {
        try {
            MultiPartData data = request.getMultiPartData();
            String content = data.getParameter("content");
            Image uploadedFile = data.getFile("image");
            String savedFileName = null;

            User findUser = Database.findUserBySessionId(request.getSessionId());

            if (findUser == null) {
                return HttpResponse.notFound(request);
            }

            if (uploadedFile != null && uploadedFile.getFileName() != null && !uploadedFile.getFileName().isEmpty()) {
                String originalFilename = uploadedFile.getFileName();
                String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                savedFileName = UUID.randomUUID() + ext;

                File dir = new File(CommonConfig.UPLOAD_DIR);
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

    public HttpResponse getArticlePage(HttpRequest request) {
        try {
            String html = Files.readString(Path.of(CommonConfig.baseDirectory + "/index.html"));
            Article article = Database.findLatest();

            if (article != null) {
                String imgTagSrc = article.getImagePath() != null ? "/asset/" + article.getImagePath() : "";
                html = html.replace("{{postImage}}", imgTagSrc);
                html = html.replace("{{postContent}}", article.getContent());
                User writer = Database.findUserById(article.getUserId());

                String writerName = "Unknown";
                String writerImage = "./img/basic_profileImage.svg";

                if (writer != null) {
                    writerName = writer.getName();
                    if (writer.getProfileImage() != null) {
                        writerImage = writer.getProfileImage();
                    }
                }

                html = html.replace("{{writerName}}", writerName);
                html = html.replace("{{writerImage}}", writerImage);

            } else {
                html = html.replace("{{postImage}}", "");
                html = html.replace("{{postContent}}", "게시글이 없습니다.");
                html = html.replace("{{writerName}}", "");
                html = html.replace("{{writerImage}}", "./img/basic_profileImage.svg");
            }

            html = DhtmlUtil.applyDynamicHeader(html, request);

            return HttpResponse.builder(request)
                    .status(HttpStatus.OK)
                    .contentType(ContentType.HTML)
                    .body(html.getBytes(StandardCharsets.UTF_8))
                    .build();
        } catch (IOException e) {
            return HttpResponse.redirect(request, "/error/500.html");
        }
    }
}
