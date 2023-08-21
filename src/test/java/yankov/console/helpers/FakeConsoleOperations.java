package yankov.console.helpers;

import yankov.console.Const;
import yankov.console.Key;
import yankov.console.operations.ConsoleOperations;
import yankov.jfp.structures.Either;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class FakeConsoleOperations extends ConsoleOperations {
    private String output;
    private final Stack<Either<String, Key>> commands;

    public FakeConsoleOperations() {
        this.output = "";
        this.commands = new Stack<>();
    }

    public String getOutput() {
        return output;
    }

    public void setCommands(List<String> commands) {
        this.commands.clear();
        for (int i = commands.size() - 1; i >= 0; i--) {
            this.commands.push(Either.rightOf(Key.ENTER));
            this.commands.push(Either.leftOf(commands.get(i)));
        }
    }

    public boolean allExecuted() {
        return commands.isEmpty();
    }

    @Override
    public void clearConsole() {
        output = "";
    }

    @Override
    public void writeln(String s) {
        output += s + Const.NEW_LINE;
    }

    @Override
    public void write(String s) {
        output += s;
    }

    @Override
    public Supplier<String> consoleReadLine() {
        throw new RuntimeException("Unexpected call to consoleReadLine");
    }

    @Override
    public void writeError(String s) {
    }

    @Override
    public Either<String, Key> readKey() {
        return commands.pop();
    }
}
