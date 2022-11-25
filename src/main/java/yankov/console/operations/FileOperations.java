package yankov.console.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileOperations {
    private final ConsoleOperations consoleOperations;

    public FileOperations(ConsoleOperations consoleOperations) {
        this.consoleOperations = consoleOperations;
    }

    public Optional<String> readFile(Path file) {
        try {
            return Optional.of(Files.readString(file));
        } catch (IOException e) {
            consoleOperations.writeError("Unable to read file: " + e.getMessage());
            return Optional.empty();
        }
    }

    public void writeFile(Path file, String contents) {
        try {
            Files.writeString(file, contents);
        } catch (IOException e) {
            consoleOperations.writeError("Unable to write file: " + e.getMessage());
        }
    }
}
