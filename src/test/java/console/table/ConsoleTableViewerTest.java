package console.table;

import console.Key;
import org.junit.Test;
import util.TestHelpers;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTableViewerTest {
    @Test
    public void show() {
        List<TestData> testData = List.of(
                new TestData("test.csv", "show-row-indexes", List.of(Key.ESC), List.of(Key.F1, Key.ESC)),
                new TestData("test.csv", "hide-row-indexes", List.of(Key.F1, Key.ESC), List.of(Key.F1, Key.ESC)),
                new TestData("test.csv", "on-tab", List.of(Key.ESC), List.of(Key.TAB, Key.ESC)),
                new TestData("test.csv", "on-left", List.of(Key.TAB, Key.ESC), List.of(Key.LEFT, Key.ESC)),
                new TestData("test.csv", "on-right", List.of(Key.ESC), List.of(Key.RIGHT, Key.ESC)),
                new TestData("test.csv", "on-up", tabsAndEsc(5), List.of(Key.UP, Key.ESC)),
                new TestData("test.csv", "on-down", List.of(Key.ESC), List.of(Key.DOWN, Key.ESC)),
                new TestData("test.csv", "on-home", tabsAndEsc(4), List.of(Key.HOME, Key.ESC)),
                new TestData("test.csv", "on-end", List.of(Key.ESC), List.of(Key.END, Key.ESC)),
                new TestData("multi-page.csv", "on-page-down", List.of(Key.ESC), List.of(Key.PAGE_DOWN, Key.ESC)),
                new TestData("multi-page.csv", "on-page-up", List.of(Key.PAGE_DOWN, Key.ESC), List.of(Key.PAGE_UP, Key.ESC)),
                new TestData("empty.csv", "empty-file", List.of(Key.ESC), List.of(Key.ESC)),
                new TestData("empty-table.csv", "empty-table", List.of(Key.ESC), List.of(Key.ESC)),
                new TestData("quotes.csv", "quotes-csv", List.of(Key.ESC), List.of(Key.ESC))
        );

        TestHelpers.testConsoleTable(
                "console-table-viewer",
                testData,
                TestHelpers::getResourceTable,
                (t, l, c, f, fOps, cOps) -> new ConsoleTableViewer<>(t, l, c, cOps)
        );
    }

    private List<Key> tabsAndEsc(int size) {
        List<Key> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(Key.TAB);
        }
        result.add(Key.ESC);
        return result;
    }
}
