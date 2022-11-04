package util;

import console.Const;
import console.table.*;
import org.junit.Assert;

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

    public static Table<String> getResourceTable(String tableName) {
        return TableParser.fromCsv(TestHelpers.readResource("table", tableName));
    }

    public static <T, E extends ConsoleTableViewer<?>> void testConsoleTable(String dir, List<TestData> testData, Function<String, Table<T>> newTable, NewConsoleTable<T, E> newConsoleTable) {
        List<String> errors = new ArrayList<>();

        for (TestData td : testData) {
            FakeConsoleOperations consoleOperations = new FakeConsoleOperations();
            consoleOperations.setInputSeq(td.getCommandsBefore());

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
            consoleTable.show();

            String beforeMessage = td.getTestName() + "-before";
            String beforeFile = beforeMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(dir, beforeFile))) {
                TestHelpers.acceptAsResource(dir, beforeFile, consoleOperations.getOutput());
                errors.add(beforeFile + " does not exist and was accepted as resource");
            } else {
                Assert.assertEquals(
                        beforeMessage,
                        TestHelpers.readResource(dir, beforeMessage + ".txt"),
                        consoleOperations.getOutput()
                );
            }

            consoleOperations.setInputSeq(td.getCommandsAfter());

            consoleTable.setMode(Mode.SELECT);
            consoleTable.show();

            String afterMessage = td.getTestName() + "-after";
            String afterFile = afterMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(dir, afterFile))) {
                TestHelpers.acceptAsResource(dir, afterFile, consoleOperations.getOutput());
                errors.add(afterFile + " does not exist and was accepted as resource");
            } else {
                Assert.assertTrue(afterMessage, consoleOperations.allExecuted());
                Assert.assertEquals(
                        afterMessage,
                        TestHelpers.readResource(dir, afterMessage + ".txt"),
                        consoleOperations.getOutput()
                );
            }
        }

        if (!errors.isEmpty()) {
            System.out.println(String.join(Const.NEW_LINE, errors));
            Assert.fail("Test failures.");
        }
    }
}