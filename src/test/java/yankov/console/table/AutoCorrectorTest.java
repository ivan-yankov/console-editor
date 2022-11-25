package yankov.console.table;

import org.junit.Assert;
import org.junit.Test;
import yankov.console.table.AutoCorrector;

import java.util.List;

public class AutoCorrectorTest {
    @Test
    public void autoCorrectUserInput_DecimalSymbol_CommaAsDecimalSymbol_ReplacedWithDot() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0.", "1.0", "2.350", ""));
        String expected = "3.86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_DotAsDecimalSymbol_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0.", "1.0", "2.350", ""));
        String expected = "3.86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NoDecimalSymbol_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0.", "1.0", "2.350", ""));
        String expected = "386";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("386"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotNumberInput_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0.", "1.0", "2.350", ""));
        String expected = "3,86i";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86i"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotOnlyDecimalNumbersInColumn_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0.", "1.0", "2,350", ""));
        String expected = "3,86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotOnlyDecimalNumbersInColumn2_ReplacedWithDot() {
        AutoCorrector corrector = new AutoCorrector(true, List.of("0", "0.", "1.0", "2.350", ""));
        String expected = "3,86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_FlagFalse_NoChange() {
        AutoCorrector corrector = new AutoCorrector(false, List.of("0.", "1.0", "2.350", ""));
        String expected = "3,86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }
}
