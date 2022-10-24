package console.editor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final List<List<String>> data;

    public Table(List<List<String>> data) {
        this.data = data;
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

    public void swapRows(Integer i, Integer j) {
        for(int c = 0; c < getColCount(); c++) {
            String tmp = data.get(i).get(c);
            data.get(i).set(c, data.get(j).get(c));
            data.get(j).set(c, tmp);
        }
    }

    public void insertRowAt(Integer index) {
        data.add(index, data.get(0).stream().map(x -> "").collect(Collectors.toList()));
    }

    public void deleteRow(int row) {
        data.remove(row);
    }
}
