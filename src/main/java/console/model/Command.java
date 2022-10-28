package console.model;

public class Command {
    private final Action action;
    private final String label;

    public Command(Action action, String label) {
        this.action = action;
        this.label = label;
    }

    public Action getAction() {
        return action;
    }

    public String getLabel() {
        return label;
    }
}
