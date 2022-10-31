package console.operations;

import java.nio.file.Path;
import java.util.Optional;

public interface FileOperations {
    Optional<String> readFile(Path file);

    void writeFile(Path file, String contents);
}
