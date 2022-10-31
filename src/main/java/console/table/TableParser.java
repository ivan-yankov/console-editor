package console.table;

import console.Const;
import console.factory.TableFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TableParser {
    public static Table<String> fromCsv(String csv) {
        if (csv.isEmpty()) return TableFactory.createEmptyStringTable();

        String[] csvLines = csv.split(Const.NEW_LINE);

        String firstLine = csvLines[0].trim();
        boolean quotesWrapped = firstLine.startsWith(Const.QUOTES) && firstLine.endsWith(Const.QUOTES);

        List<List<String>> data = Arrays
                .stream(csvLines)
                .map(String::trim)
                .map(x -> parseLine(x, quotesWrapped))
                .collect(Collectors.toList());

        if (data.size() > 0) {
            return TableFactory.createStringTable(
                    data.get(0),
                    data.subList(1, data.size()),
                    quotesWrapped
            );
        } else {
            return TableFactory.createEmptyStringTable();
        }
    }

    private static List<String> parseLine(String line, boolean quotesWrapped) {
        List<String> columns;

        if (quotesWrapped) {
            String escapedQuotesReplacement = UUID.randomUUID().toString();
            String separator = UUID.randomUUID().toString();
            String[] cols = line
                    .replace(Const.ESCAPED_QUOTES, escapedQuotesReplacement)
                    .replace("\",\"", separator)
                    .replace(Const.QUOTES, "")
                    .split(separator, -1);
            columns = Arrays.stream(cols)
                    .map(x -> x.replace(escapedQuotesReplacement, Const.ESCAPED_QUOTES))
                    .collect(Collectors.toList());
        } else {
            columns = Arrays.asList(line.split(Const.COMMA, -1));
        }

        return columns;
    }
}
