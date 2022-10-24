package console.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Table {
    private final List<List<String>> data = new ArrayList<>();

    public Table(String[][] data) {
        for (String[] row : data) {
            this.data.add(Arrays.asList(row));
        }
    }

    public List<String> getHeader() {
        return data.get(0);
    }

    public Stream<List<String>> dataStream() {
        return data.stream();
    }

    public String getCellValue(Integer row, Integer col) {
        return data.get(row).get(col);
    }

    public void setCellValue(String value, Integer row, Integer col) {
        data.get(row).set(col, value);
    }

    public Integer getRowCount() {
        return data.size();
    }

    public Integer getColCount() {
        return data.get(0).size();
    }

    public boolean hasData() {
        return data.size() > 1;
    }

    public boolean isValid() {
        return dataStream().allMatch(row -> row.size() == data.get(0).size());
    }

    public Integer fieldSize(Integer colIndex) {
        return data.stream().map(row -> row.get(colIndex)).map(String::length).max(Comparator.naturalOrder()).orElse(0);
    }
}
