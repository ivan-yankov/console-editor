package console.table;

import console.Const;
import console.Key;
import console.operations.ConsoleOperations;
import either.Either;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class FakeConsoleOperations extends ConsoleOperations {
    private final Supplier<String> input;
    private String output;
    private String error;
    private final Stack<Either<String, Key>> inputSeq;

    public FakeConsoleOperations() {
        this.input = () -> "";
        this.output = "";
        this.error = "";
        this.inputSeq = new Stack<>();
    }

    public Supplier<String> getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public void setInputSeq(List<Key> inputKeys) {
        inputSeq.clear();
        for (int i = inputKeys.size() - 1; i >= 0; i--) {
            this.inputSeq.push(Either.right(inputKeys.get(i)));
        }
    }

    public boolean allExecuted() {
        return inputSeq.isEmpty();
    }

    @Override
    public void clearConsole() {
        output = "";
    }

    @Override
    public void writeln() {
        output += Const.NEW_LINE;
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
        return input;
    }

    @Override
    public void writeError(String s) {
        error += s;
    }

    @Override
    public Either<String, Key> readKey() {
        return inputSeq.pop();
    }
}
