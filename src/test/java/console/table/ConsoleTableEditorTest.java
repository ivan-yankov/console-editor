package console.table;

import console.Key;
import org.junit.Test;
import util.TestHelpers;

import java.util.List;

public class ConsoleTableEditorTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "move-up", List.of(Key.ESC), List.of(Key.F5, Key.ESC)),
                new TestData("test.csv", "move-down", List.of(Key.ESC), List.of(Key.F6, Key.ESC)),
                new TestData("test.csv", "insert-after", List.of(Key.ESC), List.of(Key.F7, Key.ESC)),
                new TestData("test.csv", "delete-row", List.of(Key.ESC), List.of(Key.F8, Key.ESC)),
                new TestData("test.csv", "delete-column", List.of(Key.ESC), List.of(Key.CTRL_DELETE, Key.ESC)),
                new TestData("test.csv", "cut", List.of(Key.ESC), List.of(Key.CTRL_X, Key.ESC)),
                new TestData("test.csv", "copy", List.of(Key.ESC), List.of(Key.CTRL_C, Key.ESC)),
                new TestData("test.csv", "paste", List.of(Key.ESC), List.of(Key.CTRL_V, Key.ESC)),
                new TestData("test.csv", "delete", List.of(Key.ESC), List.of(Key.DELETE, Key.ESC))
        );

        TestHelpers.testConsoleTable(
                "console-table-editor",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableEditor(t, f, l, c, cOps, fOps)
        );
    }
}
