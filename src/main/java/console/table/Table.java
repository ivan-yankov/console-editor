package console.table;

import console.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Table<T> {
    private final List<Cell<String>> header;
    private final List<List<Cell<T>>> data;
    private final Supplier<Cell<T>> emptyCell;
    private final List<String> errors;

    public Table(List<Cell<String>> header, List<List<Cell<T>>> data, Supplier<Cell<T>> emptyCell) {
        this.header = Utils.asMutableList(header);
        this.data = Utils.asMutableList2d(data);
        this.emptyCell = emptyCell;
        this.errors = new ArrayList<>();
        validate();
    }

    public List<Cell<String>> getHeader() {
        return List.copyOf(header);
    }

    public List<List<Cell<T>>> getData() {
        return List.copyOf(data);
    }

    public List<String> getErrors() {
        return List.copyOf(errors);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public Cell<T> getCell(int row, int col) {
        return data.get(row).get(col);
    }

    public T getCellValue(int row, int col) {
        return getCell(row, col).getValue();
    }

    public void setCell(Cell<T> cell, int row, int col) {
        data.get(row).set(col, cell);
    }

    public void setCellValue(T value, int row, int col) {
        data.get(row).set(col, data.get(row).get(col).withValue(value));
    }

    public void setEmptyCellValue(int row, int col) {
        setCell(emptyCell.get(), row, col);
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColCount() {
        return header.size();
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public int fieldSize(int col) {
        int dataField = data
                .stream()
                .map(row -> row.get(col).toConsoleString())
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);
        return Math.max(header.get(col).getValue().length(), dataField);
    }

    public void swapRows(int i, int j) {
        if (!isValidRowIndex(i) || !isValidRowIndex(j)) {
            return;
        }

        for (int c = 0; c < getColCount(); c++) {
            Cell<T> tmp = data.get(i).get(c);
            data.get(i).set(c, data.get(j).get(c));
            data.get(j).set(c, tmp);
        }
    }

    public void insertEmptyRow(int index) {
        List<Cell<T>> items = header
                .stream()
                .map(x -> emptyCell.get())
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            data.add(items);
        } else {
            data.add(index, items);
        }
    }

    public void deleteRow(int row) {
        if (isValidRowIndex(row)) {
            data.remove(row);
        }
    }

    public void deleteCol(int col) {
        if (isValidColIndex(col)) {
            header.remove(col);
            data.forEach(x -> x.remove(col));
        }
    }

    public void updateData(List<List<Cell<T>>> data) {
        this.data.clear();
        this.data.addAll(data);
        validate();
    }

    private boolean isValidRowIndex(int index) {
        return index >= 0 && index < getRowCount();
    }

    private boolean isValidColIndex(int index) {
        return index >= 0 && index < getColCount();
    }

    private void validate() {
        Utils.zipWithIndex(data.stream())
                .filter(x -> x.getKey().size() != getColCount())
                .forEach(x -> errors.add(Utils.wrongNumberOfColumnsMessage(x.getValue() + 2, getColCount(), x.getKey().size())));
    }
}
