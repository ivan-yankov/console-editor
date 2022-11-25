package yankov.console.table;

import yankov.console.Const;
import yankov.console.factory.CellFactory;
import yankov.jutils.functional.Either;
import yankov.jutils.functional.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableParser {
    public static Either<String, Table<String>> fromCsv(String csv) {
        if (csv.isEmpty()) {
            return Either.right(Table.empty(CellFactory::createEmptyStringCell));
        }

        String[] csvLines = csv.split(Const.NEW_LINE);

        List<List<Cell<String>>> data = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < csvLines.length; i++) {
            Optional<ImmutableList<Cell<String>>> parsed = parseCsvLine(csvLines[i].trim());
            if (parsed.isPresent()) {
                data.add(parsed.get());
            } else {
                errors.add("Unable to parse line " + (i + 1) + " of the csv file");
            }
        }

        if (!errors.isEmpty()) {
            return Either.left(String.join(Const.NEW_LINE, errors));
        }

        if (!data.isEmpty()) {
            return Table.from(
                    ImmutableList.of(data.get(0)),
                    ImmutableList.fromList2d(data.subList(1, data.size())),
                    CellFactory::createEmptyStringCell
            );
        } else {
            return Either.left("Unable to parse CSV table");
        }
    }

    public static Optional<ImmutableList<Cell<String>>> parseCsvLine(String line) {
        List<Cell<String>> cells = new ArrayList<>();

        String current = line;
        while (!current.isEmpty()) {
            boolean quotesWrapped = current.startsWith(Const.QUOTES);
            int ci = -1;
            if (quotesWrapped) {
                int qi = current.indexOf(Const.QUOTES, 1);
                if (qi < 0) {
                    return Optional.empty();
                }
                do {
                    ci = current.indexOf(Const.COMMA, ci + 1);
                } while (ci >= 0 && ci < qi);
            } else {
                ci = current.indexOf(Const.COMMA);
            }

            String cellValue;
            if (ci < 0) {
                cellValue = current;
                current = "";
            } else {
                cellValue = current.substring(0, ci);
                current = current.substring(ci + 1);
            }

            if (quotesWrapped) {
                cellValue = cellValue.replace(Const.QUOTES, "");
            }

            cells.add(new Cell<>(cellValue, quotesWrapped, x -> x));
        }

        if (line.endsWith(Const.COMMA)) {
            cells.add(CellFactory.createEmptyStringCell());
        }

        return Optional.of(ImmutableList.of(cells));
    }
}
