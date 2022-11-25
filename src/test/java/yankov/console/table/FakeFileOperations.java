package yankov.console.table;

import yankov.console.operations.ConsoleOperations;
import yankov.console.operations.FileOperations;

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
