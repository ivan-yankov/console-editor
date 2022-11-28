package yankov.console.table.viewer;

import org.junit.Assert;
import org.junit.Test;

public class AutoCorrectorTest {
    @Test
    public void autoCorrectUserInput_DecimalSymbol_CommaAsDecimalSymbol_ReplacedWithDot() {
        AutoCorrector corrector = new AutoCorrector(true, null);
        String expected = "3.86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_DotAsDecimalSymbol_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, null);
        String expected = "3.86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotDecimalInput_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, null);
        String expected = "386";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("386"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotNumberInput_NoChange() {
        AutoCorrector corrector = new AutoCorrector(true, null);
        String expected = "3,86i";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86i"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbol_NotAllowed_NoChange() {
        AutoCorrector corrector = new AutoCorrector(false, null);
        String expected = "3,86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalFormat_FormattedNumber() {
        AutoCorrector corrector = new AutoCorrector(false, 3);
        String expected = "3.800";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.8"));
    }

    @Test
    public void autoCorrectUserInput_DecimalFormat_ZeroDecimalPlaces_AsInteger() {
        AutoCorrector corrector = new AutoCorrector(false, 0);
        String expected = "4";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.5"));
    }

    @Test
    public void autoCorrectUserInput_DecimalFormat_NotDecimalInput_NoChange() {
        AutoCorrector corrector = new AutoCorrector(false, 2);
        String expected = "386";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("386"));
    }

    @Test
    public void autoCorrectUserInput_DecimalFormat_NotNumberInput_NoChange() {
        AutoCorrector corrector = new AutoCorrector(false, 2);
        String expected = "3.86i";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.86i"));
    }

    @Test
    public void autoCorrectUserInput_DecimalFormat_NotAllowed_NoChange() {
        AutoCorrector corrector = new AutoCorrector(false, null);
        String expected = "3.86";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3.86"));
    }

    @Test
    public void autoCorrectUserInput_DecimalSymbolAndFormat_Succeed() {
        AutoCorrector corrector = new AutoCorrector(true, 3);
        String expected = "3.000";
        Assert.assertEquals(expected, corrector.autoCorrectUserInput("3,"));
    }
}
