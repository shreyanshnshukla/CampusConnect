package campusconnect.test;

import campusconnect.util.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/** Gives each test its own throwaway data directory so tests never touch real application data. */
public final class TestUtil {
    private TestUtil() {}

    public static FileManager freshFileManager(String testName) {
        Path dir = Paths.get("test-data", testName);
        deleteRecursively(dir);
        return new FileManager(dir.toString());
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) return;
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException ignored) {
                            // best-effort cleanup
                        }
                    });
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }
}
