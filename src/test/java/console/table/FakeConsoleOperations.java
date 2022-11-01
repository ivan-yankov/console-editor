package console.table;

import console.Const;
import console.Key;
import console.operations.ConsoleOperations;
import either.Either;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class FakeConsoleOperations extends ConsoleOperations {
    private Supplier<String> input;
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

    public void setInput(Supplier<String> input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setInputSeq(List<Either<String, Key>> inputSeq) {
        this.inputSeq.clear();
        for (int i = inputSeq.size() - 1; i >= 0; i--) {
            this.inputSeq.push(inputSeq.get(i));
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
