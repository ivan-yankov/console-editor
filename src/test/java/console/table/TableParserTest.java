package console.table;

import console.Const;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableParserTest {
    @Test
    public void parseCsvLine_WithoutQuotes_Succeed() {
        String line = "Column 1,Column 2,Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        assertEqualsList(expected, result.get().stream().map(Cell::getValue).collect(Collectors.toList()));
        Assert.assertFalse(result.get().stream().allMatch(x -> x.toCsvString().startsWith(Const.QUOTES) && x.toCsvString().endsWith(Const.QUOTES)));
    }

    @Test
    public void parseCsvLine_WithAllQuotes_Succeed() {
        String line = "\"Column 1\",\"Column 2\",\"Column 3\"";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        assertEqualsList(expected, result.get().stream().map(Cell::getValue).collect(Collectors.toList()));
        Assert.assertTrue(result.get().stream().allMatch(x -> x.toCsvString().startsWith(Const.QUOTES) && x.toCsvString().endsWith(Const.QUOTES)));
    }

    @Test
    public void parseCsvLine_WithSomeQuotes_Succeed() {
        String line = "Column 1,\"Column 2\",Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        assertEqualsList(expected, result.get().stream().map(Cell::getValue).collect(Collectors.toList()));
        assertEqualsList(
                List.of(false, true, false),
                result.get().stream().map(x -> x.toCsvString().startsWith(Const.QUOTES) && x.toCsvString().endsWith(Const.QUOTES)).collect(Collectors.toList())
        );
    }

    @Test
    public void parseCsvLine_WithQuotesEscapedDelimiter_Succeed() {
        String line = "Column 1,\"Column, 2\",Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column, 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        assertEqualsList(expected, result.get().stream().map(Cell::getValue).collect(Collectors.toList()));
        assertEqualsList(
                List.of(false, true, false),
                result.get().stream().map(x -> x.toCsvString().startsWith(Const.QUOTES) && x.toCsvString().endsWith(Const.QUOTES)).collect(Collectors.toList())
        );
    }

    @Test
    public void parseCsvLine_UnclosedQuotes_Fail() {
        String line = "Column 1,\"Column, 2,Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column, 2", "Column 3");

        Assert.assertFalse(result.isPresent());
    }

    private static <T> void assertEqualsList(List<T> l1, List<T> l2) {
        Assert.assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            Assert.assertEquals(l1.get(i), l2.get(i));
        }
    }
}
