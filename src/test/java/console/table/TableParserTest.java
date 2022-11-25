package console.table;

import console.Const;
import org.junit.Assert;
import org.junit.Test;
import yankov.jutils.functional.ImmutableList;

import java.util.Optional;

public class TableParserTest {
    @Test
    public void parseCsvLine_WithoutQuotes_Succeed() {
        String line = "Column 1,Column 2,Column 3";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithAllQuotes_Succeed() {
        String line = "\"Column 1\",\"Column 2\",\"Column 3\"";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertTrue(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithSomeQuotes_Succeed() {
        String line = "Column 1,\"Column 2\",Column 3";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(1).limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().skip(2).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithQuotesEscapedDelimiter_Succeed() {
        String line = "Column 1,\"Column, 2\",Column 3";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column, 2", "Column 3");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(1).limit(1).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().skip(2).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_UnclosedQuotes_Fail() {
        String line = "Column 1,\"Column, 2,Column 3";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void parseCsvLine_WithEmptyLastCell_Succeed() {
        String line = "Column 1,Column 2,Column 3,";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column 2", "Column 3", "");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    @Test
    public void parseCsvLine_WithEmptyLastQuotedCell_Succeed() {
        String line = "Column 1,Column 2,Column 3,\"\"";
        Optional<ImmutableList<Cell<String>>> result = TableParser.parseCsvLine(line);

        ImmutableList<String> expected = ImmutableList.from("Column 1", "Column 2", "Column 3", "");

        Assert.assertTrue(result.isPresent());
        Assert.assertTrue(expected.eq(result.get().stream().map(Cell::getValue).toList()));
        Assert.assertFalse(result.get().stream().limit(3).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertTrue(result.get().stream().skip(3).allMatch(x -> isQuotesWrapped(x.toCsvString())));
        Assert.assertFalse(result.get().stream().allMatch(x -> isQuotesWrapped(x.toConsoleString())));
    }

    private boolean isQuotesWrapped(String s) {
        return s.startsWith(Const.QUOTES) && s.endsWith(Const.QUOTES);
    }
}
