package console.table;

import console.Utils;
import console.factory.DataFactory;
import console.factory.TableFactory;
import org.junit.Assert;
import org.junit.Test;
import util.TestHelpers;

import java.time.LocalDate;
import java.util.List;

public class ConsoleDateSelectorTest {
    @Test
    public void show() {
        LocalDate date = LocalDate.of(2022, 11, 2);
        LocalDate firstDayOfMonth = Utils.firstDayOfMonth(date);
        LocalDate expectedDate = LocalDate.of(2022, 11, 3);

        List<TestData> testData = List.of(
                new TestData("test.csv", "accept", List.of("exit"), List.of("right", "enter")),
                new TestData("test.csv", "next-month", List.of("exit"), List.of("page-down", "exit")),
                new TestData("test.csv", "prev-month", List.of("exit"), List.of("page-up", "exit"))
        );

        TestHelpers.testConsoleTable(
                "console-date-selector",
                testData,
                (tn) -> TableFactory.createDateTable(
                        DataFactory.createHeaderForDateConsoleSelector(),
                        DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)
                ),
                (table, lines, columns, file, fOps, cOps) -> new ConsoleDateSelector(
                        table,
                        lines,
                        columns,
                        firstDayOfMonth,
                        () -> date,
                        d -> Assert.assertTrue(d.isEqual(expectedDate)),
                        cOps
                )
        );
    }
}
