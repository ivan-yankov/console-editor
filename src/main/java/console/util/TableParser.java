package console.util;

import console.Const;
import console.editor.Table;

import java.util.Arrays;

public class TableParser {
    public static Table fromCsv(String csv) {
        String[][] data = Arrays
                .stream(csv.split(Const.NEW_LINE))
                .map(line -> line.split(Const.COMMA))
                .toArray(String[][]::new);

        return new Table(data);
    }
}
