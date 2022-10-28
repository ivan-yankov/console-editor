package console.editor;

import console.Key;

public class Command {
    private final Mode mode;
    private final Key key;
    private final Action action;
    private final String description;

    public Command(Mode mode, Key key, Action action, String description) {
        this.mode = mode;
        this.key = key;
        this.action = action;
        this.description = description;
    }

    public Mode getMode() {
        return mode;
    }

    public Key getKey() {
        return key;
    }

    public Action getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }
}
