package console.util;

import console.Const;
import console.editor.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableParser {
    public static Table<String> fromCsv(String csv) {
        List<List<String>> data = Arrays
                .stream(csv.split(Const.NEW_LINE))
                .map(line -> new ArrayList<>(Arrays.asList(line.split(Const.COMMA, -1))))
                .collect(Collectors.toList());
        if (data.size() > 0) {
            return TableFactory.createStringTable(data.get(0), data.subList(1, data.size()));
        } else {
            return TableFactory.createEmptyStringTable();
        }
    }
}
