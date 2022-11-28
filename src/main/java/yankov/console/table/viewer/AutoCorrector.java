package yankov.console.table.viewer;

import yankov.console.Const;

import java.util.function.Function;

public class AutoCorrector {
    private final boolean correctDecimalSymbol;
    private final Integer decimalPlaces;

    public AutoCorrector(boolean correctDecimalSymbol, Integer decimalPlaces) {
        this.correctDecimalSymbol = correctDecimalSymbol;
        this.decimalPlaces = decimalPlaces;
    }

    public String autoCorrectUserInput(String input) {
        String result = applyCorrection(
                isDecimal(input) && correctDecimalSymbol,
                input,
                x -> x.replace(Const.COMMA, Const.DOT)
        );
        result = applyCorrection(
                isDecimal(result) && decimalPlaces != null,
                result,
                x -> String.format("%." +  decimalPlaces + "f", Double.parseDouble(x))
        );
        return result;
    }

    private String applyCorrection(boolean flag, String s, Function<String, String> corrector) {
        if (flag) {
            return corrector.apply(s);
        }
        return s;
    }

    private boolean isDecimal(String s) {
        boolean validSymbols = s.chars().allMatch(x -> Character.isDigit(x) || isDecimalSymbol(x));
        boolean exactlyOneDecimalSymbol = s.chars().filter(this::isDecimalSymbol).count() == 1;
        return validSymbols && exactlyOneDecimalSymbol;
    }

    private boolean isDecimalSymbol(int character) {
        return character == '.' || character == ',';
    }
}
