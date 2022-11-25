package yankov.console.helpers;

import java.util.List;

public class TestData {
    private final String tableName;
    private final String testName;
    private final List<String> commandsBefore;
    private final List<String> commandsAfter;

    public TestData(String tableName, String testName, List<String> commandsBefore, List<String> commandsAfter) {
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

    public List<String> getCommandsBefore() {
        return commandsBefore;
    }

    public List<String> getCommandsAfter() {
        return commandsAfter;
    }
}
