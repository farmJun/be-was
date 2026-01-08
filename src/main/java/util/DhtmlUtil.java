package util;

import config.CommonConfig;
import db.Database;
import http.ContentType;
import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DhtmlUtil {

    public static boolean supports(String path) {
        return path.endsWith(".html") && path.equals("/index.html");
    }

    public static HttpResponse renderForLoginUser(HttpRequest request) throws IOException {
        String html = Files.readString(
                Path.of(CommonConfig.baseDirectory + "/index.html")
        );

        String headerMenu;

        if (HttpUtil.isLoggedIn(request)) {
            User user = Database.findUserBySessionId(request.getSessionId());
            headerMenu = """
                        <li class="header__menu__item">
                          <a class="btn btn_ghost btn_size_s" href="/mypage/index.html">
                            %s님
                          </a>
                        </li>
                    """.formatted(user.getName());
        } else {
            headerMenu = """
                        <li class="header__menu__item">
                          <a class="btn btn_contained btn_size_s" href="/login/index.html">로그인</a>
                        </li>
                        <li class="header__menu__item">
                          <a class="btn btn_ghost btn_size_s" href="/registration/index.html">
                            회원 가입
                          </a>
                        </li>
                    """;
        }

        html = html.replace("{{HEADER_MENU}}", headerMenu);

        return HttpResponse.builder(request)
                .status(HttpStatus.OK)
                .contentType(ContentType.HTML)
                .body(html.getBytes(StandardCharsets.UTF_8))
                .build();
    }
}
