package yankov.console.table.viewer;

import org.junit.Test;
import yankov.console.helpers.TestData;
import yankov.console.helpers.TestHelpers;

import java.util.List;

import static yankov.console.helpers.TestHelpers.listOf;

public class ConsoleTableEditorTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "row-up", listOf("exit"), listOf("row-up", "exit")),
                new TestData("test.csv", "row-down", listOf("exit"), listOf("row-down", "exit")),
                new TestData("test.csv", "row-insert", listOf("exit"), listOf("row-insert", "exit")),
                new TestData("test.csv", "row-del", listOf("exit"), listOf("row-del", "exit")),
                new TestData("test.csv", "col-del", listOf("exit"), listOf("col-del", "exit")),
                new TestData("test.csv", "cut", listOf("exit"), listOf("cut", "exit")),
                new TestData("test.csv", "copy", listOf("exit"), listOf("copy", "exit")),
                new TestData("test.csv", "paste", listOf("exit"), listOf("paste", "exit")),
                new TestData("test.csv", "del", listOf("exit"), listOf("del", "exit"))
        );

        TestHelpers.testConsoleTable(
                "console-table-editor",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableEditor(t, f, l, c, cOps, fOps)
        );
    }
}
