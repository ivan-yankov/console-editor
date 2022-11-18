package console.model;

import console.Key;

public class Command {
    private final String name;
    private final Action action;
    private final String description;
    private final Key keyBinding;

    public Command(String name, Action action, String description, Key keyBinding) {
        this.name = name;
        this.action = action;
        this.description = description;
        this.keyBinding = keyBinding;
    }

    public Command(String name, Action action, String description) {
        this(name, action, description, null);
    }

    public String getName() {
        return name;
    }

    public Action getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getKeyBindingName() {
        return hasKeyBinding() ? keyBinding.getName() : "";
    }

    public boolean hasKeyBinding() {
        return keyBinding != null;
    }
}
