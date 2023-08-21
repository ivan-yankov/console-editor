package yankov.console.table;

import org.junit.Assert;
import org.junit.Test;
import yankov.console.Const;

import java.util.List;
import java.util.Optional;

import static yankov.jfp.utils.ListUtils.listEquals;

public class TableParserTest {
    @Test
    public void parseCsvLine_WithoutQuotes_Succeed() {
        String line = "Column 1,Column 2,Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithAllQuotes_Succeed() {
        String line = "\"Column 1\",\"Column 2\",\"Column 3\"";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertTrue(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithSomeQuotes_Succeed() {
        String line = "Column 1,\"Column 2\",Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(1).limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().skip(2).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithQuotesEscapedDelimiter_Succeed() {
        String line = "Column 1,\"Column, 2\",Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column, 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(1).limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().skip(2).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_UnclosedQuotes_Fail() {
        String line = "Column 1,\"Column, 2,Column 3";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void parseCsvLine_WithEmptyLastCell_Succeed() {
        String line = "Column 1,Column 2,Column 3,";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3", "");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithEmptyLastQuotedCell_Succeed() {
        String line = "Column 1,Column 2,Column 3,\"\"";
        Optional<List<Cell<String>>> result = TableParser.parseCsvLine(line);

        List<String> expected = List.of("Column 1", "Column 2", "Column 3", "");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(listEquals(expected, result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(3).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(3).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    private boolean isQuotesWrapped(String s) {
        return s.startsWith(Const.QUOTES) && s.endsWith(Const.QUOTES);
    }
}
