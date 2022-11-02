package console.table;

import console.operations.ConsoleOperations;
import console.operations.FileOperations;

import java.nio.file.Path;
import java.util.Optional;

public class FakeFileOperations extends FileOperations {
    public FakeFileOperations(ConsoleOperations consoleOperations) {
        super(consoleOperations);
    }

    @Override
    public Optional<String> readFile(Path file) {
        return Optional.empty();
    }

    @Override
    public void writeFile(Path file, String contents) {
    }
}
