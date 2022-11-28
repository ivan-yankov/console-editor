package yankov.console.table;

import yankov.console.Const;
import yankov.console.factory.CellFactory;
import yankov.console.table.viewer.TableColumnsMismatchException;
import yankov.jutils.functional.Either;
import yankov.jutils.functional.ImmutableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class Table<T> {
    private final ImmutableList<Cell<String>> header;
    private final ImmutableList<ImmutableList<Cell<T>>> data;
    private final Supplier<Cell<T>> emptyCell;

    private Table(ImmutableList<Cell<String>> header,
                  ImmutableList<ImmutableList<Cell<T>>> data,
                  Supplier<Cell<T>> emptyCell) {
        this.header = header;
        this.data = data;
        this.emptyCell = emptyCell;
    }

    public static <A> Either<String, Table<A>> from(ImmutableList<Cell<String>> header,
                                                    ImmutableList<ImmutableList<Cell<A>>> data,
                                                    Supplier<Cell<A>> emptyCell) {
        List<String> errors = validate(header.size(), data);
        if (errors.isEmpty()) {
            return Either.right(new Table<>(header, data, emptyCell));
        } else {
            return Either.left(String.join(Const.NEW_LINE, errors));
        }
    }

    public static <A> Table<A> empty(Supplier<Cell<A>> emptyCell) {
        return new Table<>(ImmutableList.from(), ImmutableList.from(), emptyCell);
    }

    private static <A> List<String> validate(int n, ImmutableList<ImmutableList<Cell<A>>> data) {
        List<String> errors = new ArrayList<>();
        data.zipWithIndex().stream()
                .filter(x -> x._1().size() != n)
                .forEach(x -> errors.add(wrongNumberOfColumnsMessage(x._2() + 2, n, x._1().size())));
        return errors;
    }

    private static String wrongNumberOfColumnsMessage(int row, int expected, int actual) {
        return "Line " + row + " of the csv file contains wrong number of columns. Expected " + expected + " actual " + actual;
    }

    public ImmutableList<Cell<String>> getHeader() {
        return header;
    }

    public ImmutableList<ImmutableList<Cell<T>>> getData() {
        return data;
    }

    public Cell<T> getCell(int row, int col) {
        return data.get(row).get(col);
    }

    public Either<String, Table<T>> withData(ImmutableList<ImmutableList<Cell<T>>> data) {
        return from(header, data, emptyCell);
    }

    public Table<T> withCell(Cell<T> cell, int row, int col) {
        return new Table<>(
                header,
                data.updateElement(row, data.get(row).updateElement(col, cell)),
                emptyCell
        );
    }

    public Table<T> withEmptyCell(int row, int col) {
        return withCell(emptyCell.get(), row, col);
    }

    public Table<T> withHeaderValue(String value, int index) {
        return new Table<>(
                header.updateElement(index, CellFactory.createStringCell(value)),
                data,
                emptyCell
        );
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColCount() {
        return header.size();
    }

    public boolean isEmpty() {
        return getRowCount() == 0 && getColCount() == 0;
    }

    public int fieldSize(int col) {
        int dataLongestField = data
                .stream()
                .map(row -> row.get(col).toConsoleString())
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);
        int result = Math.max(header.get(col).getValue().length(), dataLongestField);
        return result == 0 ? 1 : result;
    }

    public Table<T> swapRows(int i, int j) {
        return new Table<>(
                header,
                data.swapElements(i, j),
                emptyCell
        );
    }

    public Table<T> swapColumns(int i, int j) {
        return new Table<>(
                header.swapElements(i, j),
                data.stream().map(r -> r.swapElements(i, j)).toList(),
                emptyCell
        );
    }

    public Table<T> insertEmptyRow(int index) {
        Table<T> table = isEmpty()
                ? insertEmptyColumn(0)
                : this;
        return table.withData(
                table.getData()
                        .insert(index, ImmutableList.fill(table.getColCount(), emptyCell.get()))
        ).getRight().orElseThrow(TableColumnsMismatchException::new);
    }

    public Table<T> insertEmptyColumn(int index) {
        return new Table<>(
                header.insert(index, CellFactory.createEmptyStringCell()),
                data.stream().map(r -> r.insert(index, emptyCell.get())).toList(),
                emptyCell
        );
    }

    public Table<T> deleteRow(int row) {
        if (isInvalidRowIndex(row)) {
            return this;
        }

        return new Table<>(
                header,
                data.removeElement(row),
                emptyCell
        );
    }

    public Table<T> deleteCol(int col) {
        if (isInvalidColIndex(col)) {
            return this;
        }

        return new Table<>(
                header.removeElement(col),
                data.stream().map(r -> r.removeElement(col)).toList(),
                emptyCell
        );
    }

    private boolean isInvalidRowIndex(int index) {
        return index < 0 || index >= getRowCount();
    }

    private boolean isInvalidColIndex(int index) {
        return index < 0 || index >= getColCount();
    }
}
