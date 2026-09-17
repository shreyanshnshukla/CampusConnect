package campusconnect.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all raw File I/O. Repositories ask it for a resolved path under
 * its data directory and read/write plain pipe-delimited text through it.
 * A custom directory can be supplied (used by the test suite) to keep
 * test runs isolated from real application data.
 */
public final class FileManager {
    private final Path dataDir;

    public FileManager() {
        this(AppConstants.DATA_DIR);
    }

    public FileManager(String dataDirName) {
        this.dataDir = Paths.get(dataDirName);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Warning: could not create data directory - " + e.getMessage());
        }
    }

    public String resolvePath(String fileName) {
        return dataDir.resolve(fileName).toString();
    }

    public List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return lines;
        }
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error reading " + filePath + ": " + e.getMessage());
        }
        return lines;
    }

    public void writeLines(String filePath, List<String> lines) {
        Path path = Paths.get(filePath);
        try {
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing " + filePath + ": " + e.getMessage());
        }
    }
}
