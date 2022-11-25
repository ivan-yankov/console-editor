package yankov.console.table.viewer;

import yankov.console.table.Table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static yankov.console.table.viewer.ConsoleTableChangePropertyNames.TABLE;

public class ConsoleTableChangeListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TABLE)) {
            TableHistoryHolder.getInstance().addUndoState((Table<String>) evt.getOldValue());
        }
    }
}
