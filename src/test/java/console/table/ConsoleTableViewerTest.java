package console.table;

import org.junit.Test;
import util.TestHelpers;
import yankov.functional.ImmutableList;

import java.util.List;

import static util.TestHelpers.listOf;

public class ConsoleTableViewerTest {
    @Test
    public void show() {
        List<TestData> testData = ImmutableList.from(
                new TestData("test.csv", "hide-row-indexes", listOf("exit"), listOf("row-indexes off", "exit")),
                new TestData("test.csv", "show-row-indexes", listOf("row-indexes off", "exit"), listOf("row-indexes on", "exit")),
                new TestData("test.csv", "on-tab", listOf("exit"), listOf("tab", "exit")),
                new TestData("test.csv", "on-left", listOf("tab", "exit"), listOf("left", "exit")),
                new TestData("test.csv", "on-right", listOf("exit"), listOf("right", "exit")),
                new TestData("test.csv", "on-up", fillTab(5).append("exit"), listOf("up", "exit")),
                new TestData("test.csv", "on-down", listOf("exit"), listOf("down", "exit")),
                new TestData("test.csv", "on-first-col", fillTab(4).append("exit"), listOf("first-col", "exit")),
                new TestData("test.csv", "on-last-col", listOf("exit"), listOf("last-col", "exit")),
                new TestData("multi-page.csv", "on-page-down", listOf("exit"), listOf("page-down", "exit")),
                new TestData("multi-page.csv", "on-page-up", listOf("page-down", "exit"), listOf("page-up", "exit")),
                new TestData("multi-page.csv", "on-first-row", fillTab(220).append("page-down", "page-down", "exit"), listOf("first-row", "exit")),
                new TestData("multi-page.csv", "on-last-row", listOf("exit"), listOf("last-row", "exit")),
                new TestData("empty.csv", "empty-file", listOf("exit"), listOf("exit")),
                new TestData("empty-table.csv", "empty-table", listOf("exit"), listOf("exit")),
                new TestData("quotes.csv", "quotes-csv", listOf("exit"), listOf("exit"))
        );

        TestHelpers.testConsoleTable(
                "console-table-viewer",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableViewer<>(t, l, c, cOps)
        );
    }

    private ImmutableList<String> fillTab(int n) {
        return ImmutableList.fill(n, "tab");
    }
}
