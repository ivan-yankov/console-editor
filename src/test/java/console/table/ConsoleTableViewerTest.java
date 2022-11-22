package console.table;

import org.junit.Test;
import util.TestHelpers;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTableViewerTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "hide-row-indexes", List.of("exit"), List.of("indexes", "exit")),
                new TestData("test.csv", "show-row-indexes", List.of("indexes", "exit"), List.of("indexes", "exit")),
                new TestData("test.csv", "on-tab", List.of("exit"), List.of("tab", "exit")),
                new TestData("test.csv", "on-left", List.of("tab", "exit"), List.of("left", "exit")),
                new TestData("test.csv", "on-right", List.of("exit"), List.of("right", "exit")),
                new TestData("test.csv", "on-up", tabsAndExit(5), List.of("up", "exit")),
                new TestData("test.csv", "on-down", List.of("exit"), List.of("down", "exit")),
                new TestData("test.csv", "on-first-col", tabsAndExit(4), List.of("first-col", "exit")),
                new TestData("test.csv", "on-last-col", List.of("exit"), List.of("last-col", "exit")),
                new TestData("multi-page.csv", "on-page-down", List.of("exit"), List.of("page-down", "exit")),
                new TestData("multi-page.csv", "on-page-up", List.of("page-down", "exit"), List.of("page-up", "exit")),
                new TestData("empty.csv", "empty-file", List.of("exit"), List.of("exit")),
                new TestData("empty-table.csv", "empty-table", List.of("exit"), List.of("exit")),
                new TestData("quotes.csv", "quotes-csv", List.of("exit"), List.of("exit"))
        );

        TestHelpers.testConsoleTable(
                "console-table-viewer",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableViewer<>(t, l, c, cOps)
        );
    }

    private List<String> tabsAndExit(int size) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add("tab");
        }
        result.add("exit");
        return result;
    }
}
