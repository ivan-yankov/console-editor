package console.table;

import console.Key;
import either.Either;
import org.junit.Assert;
import org.junit.Test;
import util.TestHelpers;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTableViewerTest {
    private final int lines = 50;
    private final int columns = 200;

    @Test
    public void show_OnTab_ChangeCellFocus() {
        List<String> header = new ArrayList<>();
        header.add("Column 1");
        header.add("Column 2");
        header.add("Column 3");

        List<List<String>> data = new ArrayList<>();
        data.add(List.of("Data 11", "Data 12", "Data 13"));
        data.add(List.of("Data 21", "Data 22", "Data 23"));
        data.add(List.of("Data 31", "Data 32", "Data 33"));

        Table<String> table = createTable(header, data);

        FakeConsoleOperations consoleOperations = new FakeConsoleOperations();
        consoleOperations.setInputSeq(List.of(Either.right(Key.ESC)));

        ConsoleTableViewer<String> tableViewer = new ConsoleTableViewer<>(table, lines, columns, consoleOperations);
        tableViewer.setTitle("Title");
        tableViewer.show();

//        TestHelpers.acceptAsResource("console-table-viewer", "show-tab-before-command.txt", consoleOperations.getOutput());
        Assert.assertEquals(
                TestHelpers.readResource("console-table-viewer", "show-tab-before-command.txt"),
                consoleOperations.getOutput()
        );

        consoleOperations.clearInputKeySeq();
        consoleOperations.setInputSeq(List.of(Either.right(Key.TAB), Either.right(Key.ESC)));

        tableViewer.setMode(Mode.SELECT);
        tableViewer.show();

        Assert.assertTrue(consoleOperations.allExecuted());

//        TestHelpers.acceptAsResource("console-table-viewer", "show-tab-after-command.txt", consoleOperations.getOutput());
        Assert.assertEquals(
                TestHelpers.readResource("console-table-viewer", "show-tab-after-command.txt"),
                consoleOperations.getOutput()
        );
    }

    private Table<String> createTable(List<String> header, List<List<String>> data) {
        return new Table<>(header, data, x -> x, () -> "", false);
    }
}
