package console.table;

import console.Key;

import java.util.List;

public class TestData {
    private final String tableName;
    private final String testName;
    private final List<Key> commandsBefore;
    private final List<Key> commandsAfter;

    public TestData(String tableName, String testName, List<Key> commandsBefore, List<Key> commandsAfter) {
        this.tableName = tableName;
        this.testName = testName;
        this.commandsBefore = commandsBefore;
        this.commandsAfter = commandsAfter;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTestName() {
        return testName;
    }

    public List<Key> getCommandsBefore() {
        return commandsBefore;
    }

    public List<Key> getCommandsAfter() {
        return commandsAfter;
    }
}
