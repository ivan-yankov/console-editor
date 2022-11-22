package console.model;

import console.Key;

import java.util.List;
import java.util.function.Consumer;

public class Command {
    private final String name;
    private final Consumer<List<String>> action;
    private final String description;
    private final Key keyBinding;

    public Command(String name, Consumer<List<String>> action, String description, Key keyBinding) {
        this.name = name;
        this.action = action;
        this.description = description;
        this.keyBinding = keyBinding;
    }

    public Command(String name, Consumer<List<String>> action, String description) {
        this(name, action, description, null);
    }

    public String getName() {
        return name;
    }

    public Consumer<List<String>> getAction() {
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
