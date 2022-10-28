package console.table;

import console.Key;

public class CommandKey {
    private final Mode mode;
    private final Key key;

    public CommandKey(Mode mode, Key key) {
        this.mode = mode;
        this.key = key;
    }

    public Mode getMode() {
        return mode;
    }

    public Key getKey() {
        return key;
    }
}
