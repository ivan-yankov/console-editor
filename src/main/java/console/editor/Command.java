package console.editor;

import console.Key;

public class Command {
    private final Key key;
    private final Action action;
    private final String description;

    public Command(Key key, Action action, String description) {
        this.key = key;
        this.action = action;
        this.description = description;
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
