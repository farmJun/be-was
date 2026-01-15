package business;

import config.CommonConfig;
import db.Database;

import db.UserRepository;
import http.ContentType;
import http.HttpStatus;
import http.request.HttpRequest;
import http.request.MultiPartData;
import http.response.HttpResponse;

import model.Image;
import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DhtmlUtil;
import util.HttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class UserBusiness {

    private final UserRepository userRepository = new UserRepository();
    private static final Logger logger = LoggerFactory.getLogger(UserBusiness.class);

    public HttpResponse signUp(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getForm();
        String userId = body.get("userId");
        String name = body.get("name");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(userId, password, name, email);
        userRepository.save(user);

        return HttpResponse.redirect(httpRequest, "/index.html");
    }

    public HttpResponse login(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getForm();

        User findUser = userRepository.findByUserId(body.get("userId"));

        try {
            if (findUser == null) {
                return HttpResponse.loginFail(HttpStatus.OK, httpRequest);
            }

            if (!findUser.getPassword().equals(body.get("password"))) {
                return HttpResponse.loginFail(HttpStatus.OK, httpRequest);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        String sessionId = HttpUtil.generateRandomSessionId();
        Database.addUserSession(findUser, sessionId);
        return HttpResponse.redirectWithCookie(httpRequest, "/index.html", sessionId);
    }

    public HttpResponse getMyPage(HttpRequest request) {
        if (!HttpUtil.isLoggedIn(request)) {
            return HttpResponse.redirect(request, "/login/index.html");
        }

        try {
            User user = Database.findUserBySessionId(request.getSessionId());
            String html = Files.readString(Path.of("./src/main/resources/static/mypage/index.html"));

            String profileImgSrc = (user.getProfileImage() != null) ? user.getProfileImage() : "../img/basic_profileImage.svg";
            html = html.replace("{{nickname}}", user.getName());
            html = html.replace("{{profileImage}}", profileImgSrc);
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

    public HttpResponse updateUser(HttpRequest request) {
        if (!HttpUtil.isLoggedIn(request)) {
            return HttpResponse.redirect(request, "/login/index.html");
        }

        try {
            User user = Database.findUserBySessionId(request.getSessionId());
            MultiPartData data = request.getMultiPartData();
            String newNickname = data.getParameter("nickname");
            String newPassword = data.getParameter("password");

            if (newNickname != null && !newNickname.isEmpty()) {
                user.setName(newNickname);
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }

            Image file = data.getFile("profileImage");
            if (file != null && file.getFileName() != null && !file.getFileName().isEmpty()) {
                File dir = new File(CommonConfig.UPLOAD_DIR);
                String ext = file.getFileName().substring(file.getFileName().lastIndexOf("."));
                String savedFileName = UUID.randomUUID() + ext;
                File dest = new File(dir, savedFileName);

                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(file.getData());
                }

                user.setProfileImage("/asset/" + savedFileName);
            }
            userRepository.update(user);

            return HttpResponse.redirect(request, "/mypage/index.html");
        } catch (IOException e) {
            return HttpResponse.redirect(request, "/error/500.html");
        }
    }
}
