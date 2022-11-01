package console.table;

import console.Const;
import console.Key;
import org.junit.Assert;
import org.junit.Test;
import util.TestHelpers;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConsoleTableViewerTest {
    private static final int LINES = 50;
    private static final int COLUMNS = 200;
    private static final String DIR = "console-table-viewer";

    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "show-row-indexes", List.of(Key.ESC), List.of(Key.F1, Key.ESC)),
                new TestData("test.csv", "hide-row-indexes", List.of(Key.F1, Key.ESC), List.of(Key.F1, Key.ESC)),
                new TestData("test.csv", "on-tab", List.of(Key.ESC), List.of(Key.TAB, Key.ESC)),
                new TestData("test.csv", "on-left", List.of(Key.TAB, Key.ESC), List.of(Key.LEFT, Key.ESC)),
                new TestData("test.csv", "on-right", List.of(Key.ESC), List.of(Key.RIGHT, Key.ESC)),
                new TestData("test.csv", "on-up", fillAndEsc(5), List.of(Key.UP, Key.ESC)),
                new TestData("test.csv", "on-down", List.of(Key.ESC), List.of(Key.DOWN, Key.ESC)),
                new TestData("test.csv", "on-home", fillAndEsc(4), List.of(Key.HOME, Key.ESC)),
                new TestData("test.csv", "on-end", List.of(Key.ESC), List.of(Key.END, Key.ESC)),
                new TestData("multi-page.csv", "on-page-down", List.of(Key.ESC), List.of(Key.PAGE_DOWN, Key.ESC)),
                new TestData("multi-page.csv", "on-page-up", List.of(Key.PAGE_DOWN, Key.ESC), List.of(Key.PAGE_UP, Key.ESC)),
                new TestData("empty.csv", "empty-file", List.of(Key.ESC), List.of(Key.ESC)),
                new TestData("empty-table.csv", "empty-table", List.of(Key.ESC), List.of(Key.ESC)),
                new TestData("quotes.csv", "quotes-csv", List.of(Key.ESC), List.of(Key.ESC))
        );

        List<String> errors = new ArrayList<>();

        for (TestData td : testData) {
            FakeConsoleOperations consoleOperations = new FakeConsoleOperations();
            consoleOperations.setInputSeq(td.getCommandsBefore());

            ConsoleTableViewer<String> tableViewer = new ConsoleTableViewer<>(td.getTable(), LINES, COLUMNS, consoleOperations);
            tableViewer.setTitle("Title");
            tableViewer.show();

            String beforeMessage = td.getTestName() + "-before";
            String beforeFile = beforeMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(DIR, beforeFile))) {
                TestHelpers.acceptAsResource(DIR, beforeFile, consoleOperations.getOutput());
                errors.add(beforeFile + " does not exist and was accepted as resource");
            } else {
                Assert.assertEquals(
                        beforeMessage,
                        TestHelpers.readResource(DIR, beforeMessage + ".txt"),
                        consoleOperations.getOutput()
                );
            }

            consoleOperations.setInputSeq(td.getCommandsAfter());

            tableViewer.setMode(Mode.SELECT);
            tableViewer.show();

            String afterMessage = td.getTestName() + "-after";
            String afterFile = afterMessage + ".txt";

            if (!Files.exists(TestHelpers.resourcePath(DIR, afterFile))) {
                TestHelpers.acceptAsResource(DIR, afterFile, consoleOperations.getOutput());
                errors.add(afterFile + " does not exist and was accepted as resource");
            } else {
                Assert.assertTrue(afterMessage, consoleOperations.allExecuted());
                Assert.assertEquals(
                        afterMessage,
                        TestHelpers.readResource(DIR, afterMessage + ".txt"),
                        consoleOperations.getOutput()
                );
            }
        }

        if (!errors.isEmpty()) {
            Assert.fail("Test failures.");
            System.err.println(String.join(Const.NEW_LINE, errors));
        }
    }

    private List<Key> fillAndEsc(int size) {
        List<Key> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(Key.TAB);
        }
        result.add(Key.ESC);
        return result;
    }
}
