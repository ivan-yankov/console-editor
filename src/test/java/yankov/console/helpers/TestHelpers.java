package yankov.console.helpers;

import yankov.console.Const;
import org.junit.Assert;
import yankov.console.table.Table;
import yankov.console.table.TableParser;
import yankov.console.table.viewer.ConsoleTableViewer;
import yankov.console.table.viewer.Mode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestHelpers {
    private static final int LINES = 50;
    private static final int COLUMNS = 200;

    public static String readResource(String dir, String name) {
        try {
            return Files.readString(resourcePath(dir, name));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }

    public static Path resourcePath(String dir, String name) {
        return Paths.get("src", "test", "resources", dir, name);
    }

    public static Table<String> getResourceTable(String tableName) {
        return TableParser.fromCsv(TestHelpers.readResource("table", tableName)).getRight().orElseThrow();
    }

    public static <T, E extends ConsoleTableViewer<?>> void testConsoleTable(String dir, List<TestData> testData, Function<String, Table<T>> newTable, NewConsoleTable<T, E> newConsoleTable) {
        List<String> errors = new ArrayList<>();

        for (TestData td : testData) {
            FakeConsoleOperations consoleOperations = new FakeConsoleOperations();
            consoleOperations.setCommands(td.getCommandsBefore());

            E consoleTable = newConsoleTable.apply(
                    newTable.apply(td.getTableName()),
                    LINES,
                    COLUMNS,
                    Paths.get(""),
                    new FakeFileOperations(consoleOperations),
                    consoleOperations
            );

            String title = consoleTable.getTitle();
            if (title.isEmpty()) {
                title = "Title";
            }
            consoleTable.setTitle(title);
            consoleTable.setMode(Mode.COMMAND);
            consoleTable.show();

            String beforeMessage = td.getTestName() + "-before";
            String beforeFile = beforeMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(dir, beforeFile))) {
                writeResource(dir, beforeFile, consoleOperations.getOutput());
                errors.add(beforeFile + " does not exist and was accepted as resource");
            } else {
                try {
                    Assert.assertEquals(
                            TestHelpers.readResource(dir, beforeMessage + ".txt"),
                            consoleOperations.getOutput()
                    );
                } catch (Throwable t) {
                    writeResource(dir, beforeMessage + "_new.txt", consoleOperations.getOutput());
                    Assert.fail(beforeMessage);
                }
            }

            consoleOperations.setCommands(td.getCommandsAfter());

            consoleTable.setMode(Mode.COMMAND);
            consoleTable.show();

            String afterMessage = td.getTestName() + "-after";
            String afterFile = afterMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(dir, afterFile))) {
                writeResource(dir, afterFile, consoleOperations.getOutput());
                errors.add(afterFile + " does not exist and was accepted as resource");
            } else {
                try {
                    Assert.assertTrue(consoleOperations.allExecuted());
                    Assert.assertEquals(
                            TestHelpers.readResource(dir, afterMessage + ".txt"),
                            consoleOperations.getOutput()
                    );
                } catch (Throwable t) {
                    writeResource(dir, afterMessage + "_new.txt", consoleOperations.getOutput());
                    Assert.fail(afterMessage);
                }
            }
        }

        if (!errors.isEmpty()) {
            System.err.println(String.join(Const.NEW_LINE, errors));
            Assert.fail("Test failures.");
        }
    }

    private static void writeResource(String dir, String name, String s) {
        try {
            Path file = resourcePath(dir, name);
            Files.createDirectories(file.getParent());
            Files.writeString(file, s);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<String> listOf(String... s) {
        return List.of(s);
    }
}
