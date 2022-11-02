package console.table;

import console.Const;
import console.Key;
import org.junit.Assert;
import org.junit.Test;
import util.TestHelpers;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleTableTest {
    private static final int LINES = 50;
    private static final int COLUMNS = 200;

    @Test
    public void show() {
        Map<String, List<TestData>> testData = new HashMap<>();
        testData.put("console-table-viewer", List.of(
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
        ));

        testData.put("console-table-editor", List.of(
                new TestData("test.csv", "save", List.of(Key.ESC), List.of(Key.F3, Key.ESC)),
                new TestData("test.csv", "move-up", List.of(Key.ESC), List.of(Key.F5, Key.ESC)),
                new TestData("test.csv", "move-down", List.of(Key.ESC), List.of(Key.F6, Key.ESC)),
                new TestData("test.csv", "insert-after", List.of(Key.ESC), List.of(Key.F7, Key.ESC)),
                new TestData("test.csv", "delete-row", List.of(Key.ESC), List.of(Key.F8, Key.ESC)),
                new TestData("test.csv", "delete-column", List.of(Key.ESC), List.of(Key.CTRL_DELETE, Key.ESC)),
                new TestData("test.csv", "cut", List.of(Key.ESC), List.of(Key.CTRL_X, Key.ESC)),
                new TestData("test.csv", "copy", List.of(Key.ESC), List.of(Key.CTRL_C, Key.ESC)),
                new TestData("test.csv", "paste", List.of(Key.ESC), List.of(Key.CTRL_V, Key.ESC)),
                new TestData("test.csv", "delete", List.of(Key.ESC), List.of(Key.DELETE, Key.ESC))
        ));

        List<String> errors = new ArrayList<>();

        for (Map.Entry<String, List<TestData>> suite : testData.entrySet()) {
            String dir = suite.getKey();
            suite.getValue().forEach(td -> {
                        FakeConsoleOperations consoleOperations = new FakeConsoleOperations();
                        consoleOperations.setInputSeq(td.getCommandsBefore());

                        ConsoleTableViewer<String> tableViewer = new ConsoleTableViewer<>(td.getTable(), LINES, COLUMNS, consoleOperations);
                        tableViewer.setTitle("Title");
                        tableViewer.show();

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

                        tableViewer.setMode(Mode.SELECT);
                        tableViewer.show();

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
            );
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
