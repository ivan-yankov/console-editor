package console.table;

import org.junit.Test;
import util.TestHelpers;

import java.util.List;

public class ConsoleTableEditorTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "row-up", List.of("exit"), List.of("row-up", "exit")),
                new TestData("test.csv", "row-down", List.of("exit"), List.of("row-down", "exit")),
                new TestData("test.csv", "row-insert", List.of("exit"), List.of("row-insert", "exit")),
                new TestData("test.csv", "row-del", List.of("exit"), List.of("row-del", "exit")),
                new TestData("test.csv", "col-del", List.of("exit"), List.of("col-del", "exit")),
                new TestData("test.csv", "cut", List.of("exit"), List.of("cut", "exit")),
                new TestData("test.csv", "copy", List.of("exit"), List.of("copy", "exit")),
                new TestData("test.csv", "paste", List.of("exit"), List.of("paste", "exit")),
                new TestData("test.csv", "del", List.of("exit"), List.of("del", "exit"))
        );

        TestHelpers.testConsoleTable(
                "console-table-editor",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableEditor(t, f, l, c, cOps, fOps)
        );
    }
}
