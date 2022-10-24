package console.editor;

import java.util.Arrays;
import java.util.List;

public class HelpItems {
    public static final List<String> commands = Arrays.asList(
            "TAB, LEFT, RIGHT, UP, DOWN, HOME, END: Navigation",
            "F2: Edit cell",
            "F3: Save",
            "F4: Close",
            "F5: Move row up",
            "F6: Move row down",
            "F7: Append row",
            "F8: Delete row",
            "DELETE: Delete cell value"
    );

    public static final List<String> edit = Arrays.asList(
            "ESC: Discard changes",
            "ENTER: Accept new value"
    );
}
