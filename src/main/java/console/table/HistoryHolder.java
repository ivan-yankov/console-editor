package console.table;

import java.util.Stack;

public class HistoryHolder<T> {
    private final Stack<T> undo;
    private final Stack<T> redo;

    public HistoryHolder() {
        this.undo = new Stack<>();
        this.redo = new Stack<>();
    }

    public void addUndoState(T element) {
        undo.push(element);
    }

    public T undo(T current) {
        if (undo.isEmpty()) {
            return current;
        } else {
            redo.push(current);
            return undo.pop();
        }
    }

    public T redo(T current) {
        if (redo.isEmpty()) {
            return current;
        } else {
            return redo.pop();
        }
    }
}
