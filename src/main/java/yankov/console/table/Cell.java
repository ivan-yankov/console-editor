package yankov.console.table;

import yankov.console.Const;

import java.util.function.Function;

public class Cell<T> {
    private final T value;
    private final boolean quotesWrapped;
    private final Function<T, String> printValue;
    private final String color;

    public Cell(T value, boolean quotesWrapped, Function<T, String> printValue, String color) {
        this.value = value;
        this.quotesWrapped = quotesWrapped;
        this.printValue = printValue;
        this.color = color;
    }

    public Cell(T value, boolean quotesWrapped, Function<T, String> printValue) {
        this(value, quotesWrapped, printValue, "");
    }

    public T getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public String toConsoleString() {
        return print();
    }

    public String toCsvString() {
        if (quotesWrapped) {
            return Const.QUOTES + print() + Const.QUOTES;
        } else {
            return print();
        }
    }

    private String print() {
        return printValue.apply(value);
    }
}
