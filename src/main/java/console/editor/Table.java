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

    public Stream<List<T>> dataStream() {
        return data.stream();
    }

    public T getCellValue(Integer row, Integer col) {
        return data.get(row).get(col);
    }

    public void setCellValue(T value, Integer row, Integer col) {
        data.get(row).set(col, value);
    }

    public Integer getRowCount() {
        return data.size();
    }

    public Integer getColCount() {
        return header.size();
    }

    public boolean hasData() {
        return data.size() > 1;
    }

    public boolean isValid() {
        return dataStream().allMatch(row -> row.size() == header.size());
    }

    public Integer fieldSize(Integer col) {
        Integer dataField = data
                .stream()
                .map(row -> printValue.apply(row.get(col)))
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);
        return Math.max(header.get(col).length(), dataField);
    }

    public void swapRows(Integer i, Integer j) {
        for(int c = 0; c < getColCount(); c++) {
            T tmp = data.get(i).get(c);
            data.get(i).set(c, data.get(j).get(c));
            data.get(j).set(c, tmp);
        }
    }

    public void insertRowAt(Integer index) {
        data.add(index, header.stream().map(x -> emptyValue.get()).collect(Collectors.toList()));
    }

    public void deleteRow(int row) {
        data.remove(row);
    }

    public void updateData(List<List<T>> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
