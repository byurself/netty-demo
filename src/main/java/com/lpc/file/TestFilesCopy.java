package com.lpc.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author byu_rself
 * @date 2023/7/26 14:35
 */
public class TestFilesCopy {

    public static void main(String[] args) throws IOException {
        String source = "D:\\Java\\jdk-17.0.3.1\\conf";
        String target = "D:\\Java\\jdk-17.0.3.1\\conf_copy";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                if (Files.isDirectory(path)) {
                    // 目录
                    Files.createDirectories(Paths.get(targetName));
                } else if (Files.isRegularFile(path)) {
                    // 文件
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
