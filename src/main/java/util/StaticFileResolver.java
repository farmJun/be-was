package util;

import config.CommonConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileResolver {

    public static boolean exists(String path) {
        Path resolvedPath = resolvePath(path);
        return Files.exists(resolvedPath) && Files.isRegularFile(resolvedPath);
    }

    private static Path resolvePath(String path) {
        if (path.contains("..")) {
            return Path.of(CommonConfig.baseDirectory + "/404.html");

        }

        return Path.of(CommonConfig.baseDirectory + path).normalize();
    }
}
