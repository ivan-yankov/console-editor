package console.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static console.table.ConsoleTableChangePropertyNames.TABLE;

public class ConsoleTableChangeListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TABLE)) {
            TableHistoryHolder.getInstance().addUndoState((Table<String>) evt.getOldValue());
        }
    }
}
