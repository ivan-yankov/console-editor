package yankov.console.table.viewer;

import org.junit.Assert;
import org.junit.Test;
import yankov.console.Utils;
import yankov.console.factory.DataFactory;
import yankov.console.factory.TableFactory;
import yankov.console.helpers.TestData;
import yankov.console.helpers.TestHelpers;
import yankov.jutils.functional.ImmutableList;

import java.time.LocalDate;
import java.util.List;

import static yankov.console.helpers.TestHelpers.listOf;

public class ConsoleDateSelectorTest {
    @Test
    public void show() {
        LocalDate date = LocalDate.of(2022, 11, 2);
        LocalDate firstDayOfMonth = Utils.firstDayOfMonth(date);
        LocalDate expectedDate = LocalDate.of(2022, 11, 3);

        List<TestData> testData = ImmutableList.from(
                new TestData("test.csv", "accept", listOf("exit"), listOf("right", "enter")),
                new TestData("test.csv", "next-month", listOf("exit"), listOf("page-down", "exit")),
                new TestData("test.csv", "prev-month", listOf("exit"), listOf("page-up", "exit"))
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
