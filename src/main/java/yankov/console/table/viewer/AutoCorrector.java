package yankov.console.table.viewer;

import yankov.console.Const;

import java.util.List;

public class AutoCorrector {
    private final boolean correctDecimalSymbol;
    private final List<String> columnData;

    public AutoCorrector(boolean correctDecimalSymbol, List<String> columnData) {
        this.correctDecimalSymbol = correctDecimalSymbol;
        this.columnData = columnData;
    }

    public String autoCorrectUserInput(String input) {
        if (correctDecimalSymbol) {
            return isInDecimalColumn(input) ? input.replace(Const.COMMA, Const.DOT) : input;
        } else {
            return input;
        }
    }

    private boolean isInDecimalColumn(String input) {
        return columnData.stream().allMatch(x -> x.isEmpty() || isDecimal(x))
                && input.chars().allMatch(x -> Character.isDigit(x) || x == '.' || x == ',');
    }

    private boolean isDecimal(String s) {
        boolean validSymbols = s.chars().allMatch(x -> Character.isDigit(x) || x == '.');
        boolean exactlyOneDecimalSymbol = s.chars().filter(x -> x == '.').count() == 1;
        return validSymbols && exactlyOneDecimalSymbol;
    }
}
