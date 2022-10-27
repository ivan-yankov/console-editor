package console.editor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table<T> {
    private final List<String> header;
    private final List<List<T>> data;
    private final Function<T, String> printValue;
    private final Supplier<T> emptyValue;

    public Table(List<String> header, List<List<T>> data, Function<T, String> printValue, Supplier<T> emptyValue) {
        this.header = header;
        this.data = data;
        this.printValue = printValue;
        this.emptyValue = emptyValue;
    }

    public List<String> getHeader() {
        return header;
    }

    public Function<T, String> getPrintValue() {
        return printValue;
    }

    public Supplier<T> getEmptyValue() {
        return emptyValue;
    }

    public Stream<List<T>> getDataStream() {
        return data.stream();
    }

    public T getCellValue(int row, int col) {
        return data.get(row).get(col);
    }

    public void setCellValue(T value, int row, int col) {
        data.get(row).set(col, value);
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColCount() {
        return header.size();
    }

    public boolean hasData() {
        return data.size() > 1;
    }

    public boolean isValid() {
        return getDataStream().allMatch(row -> row.size() == header.size());
    }

    public int fieldSize(int col) {
        int dataField = data
                .stream()
                .map(row -> printValue.apply(row.get(col)))
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);
        return Math.max(header.get(col).length(), dataField);
    }

    public void swapRows(int i, int j) {
        for(int c = 0; c < getColCount(); c++) {
            T tmp = data.get(i).get(c);
            data.get(i).set(c, data.get(j).get(c));
            data.get(j).set(c, tmp);
        }
    }

    public void insertRowAt(int index) {
        List<T> items = header.stream().map(x -> emptyValue.get()).collect(Collectors.toList());
        if (data.isEmpty()) {
            data.add(items);
        } else {
            data.add(index, items);
        }
    }

    public void deleteRow(int row) {
        data.remove(row);
    }

    public void updateData(List<List<T>> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
