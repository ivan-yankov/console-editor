package console.table;

import org.junit.Test;
import util.TestHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsoleTableViewerTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "hide-row-indexes", List.of("exit"), List.of("indexes", "exit")),
                new TestData("test.csv", "show-row-indexes", List.of("indexes", "exit"), List.of("indexes", "exit")),
                new TestData("test.csv", "on-tab", List.of("exit"), List.of("tab", "exit")),
                new TestData("test.csv", "on-left", List.of("tab", "exit"), List.of("left", "exit")),
                new TestData("test.csv", "on-right", List.of("exit"), List.of("right", "exit")),
                new TestData("test.csv", "on-up", append(fill(5, "tab"), "exit"), List.of("up", "exit")),
                new TestData("test.csv", "on-down", List.of("exit"), List.of("down", "exit")),
                new TestData("test.csv", "on-first-col", append(fill(4, "tab"), "exit"), List.of("first-col", "exit")),
                new TestData("test.csv", "on-last-col", List.of("exit"), List.of("last-col", "exit")),
                new TestData("multi-page.csv", "on-page-down", List.of("exit"), List.of("page-down", "exit")),
                new TestData("multi-page.csv", "on-page-up", List.of("page-down", "exit"), List.of("page-up", "exit")),
                new TestData("multi-page.csv", "on-first-row", append(fill(220, "tab"), "page-down", "page-down", "exit"), List.of("first-row", "exit")),
                new TestData("multi-page.csv", "on-last-row", List.of("exit"), List.of("last-row", "exit")),
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

    private List<String> fill(int n, String s) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(s);
        }
        return result;
    }

    private List<String> append(List<String> list, String... s) {
        List<String> result = new ArrayList<>(list);
        result.addAll(List.of(s));
        return result;
    }
}
