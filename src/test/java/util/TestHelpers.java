package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelpers {
    public static String readResource(String dir, String name) {
        try {
            return Files.readString(resourcePath(dir, name));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }

    public static void acceptAsResource(String dir, String name, String s) {
        try {
            Path file = resourcePath(dir, name);
            Files.createDirectories(file.getParent());
            Files.writeString(file, s);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Path resourcePath(String dir, String name) {
        return Paths.get("src", "test", "resources", dir, name);
    }
}
