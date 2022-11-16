package console.table;

import console.Const;

import java.util.function.Function;

public class Cell<T> {
    private final T value;
    private final boolean quotesWrapped;
    private final Function<T, String> printValue;

    public Cell(T value, boolean quotesWrapped, Function<T, String> printValue) {
        this.value = value;
        this.quotesWrapped = quotesWrapped;
        this.printValue = printValue;
    }

    public T getValue() {
        return value;
    }

    public Cell<T> withValue(T newValue) {
        return new Cell<>(newValue, quotesWrapped, printValue);
    }

    public String toConsoleString() {
        return printValue.apply(value);
    }

    public String toCsvString() {
        if (quotesWrapped) {
            return Const.QUOTES + printValue.apply(value) + Const.QUOTES;
        } else {
            return printValue.apply(value);
        }
    }
}
