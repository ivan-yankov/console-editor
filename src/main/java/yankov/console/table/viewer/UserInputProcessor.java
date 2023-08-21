package yankov.console.table.viewer;

import yankov.console.Key;
import yankov.console.operations.ConsoleOperations;
import yankov.jfp.structures.Either;

import java.util.function.Consumer;
import java.util.function.Function;

public class UserInputProcessor {
    private final StringBuilder userInput;
    private final ConsoleOperations consoleOperations;
    private final Function<String, String> hint;
    private final Consumer<String> inputProcessor;
    private final Consumer<Key> keyProcessor;
    private final Runnable enterProcessor;
    private final Runnable resetMode;

    public UserInputProcessor(ConsoleOperations consoleOperations,
                              Function<String, String> hint,
                              Consumer<String> inputProcessor,
                              Consumer<Key> keyProcessor,
                              Runnable enterProcessor,
                              Runnable resetMode) {
        this.userInput = new StringBuilder();
        this.consoleOperations = consoleOperations;
        this.hint = hint;
        this.inputProcessor = inputProcessor;
        this.keyProcessor = keyProcessor;
        this.enterProcessor = enterProcessor;
        this.resetMode = resetMode;
    }

    public String getUserInput() {
        return userInput.toString();
    }

    private void resetUserInput() {
        userInput.setLength(0);
    }

    public void processUserInput() {
        Either<String, Key> input = consoleOperations.readKey();
        if (input.getLeft().isPresent()) {
            userInput.append(input.getLeft().get().trim());
        } else {
            Key key = input.getRight().orElse(Key.UNKNOWN);
            switch (key) {
                case ESC:
                    if (!getUserInput().isEmpty()) {
                        resetUserInput();
                    } else {
                        resetMode.run();
                    }
                    break;
                case ENTER:
                    if (!getUserInput().isEmpty()) {
                        inputProcessor.accept(getUserInput());
                        resetUserInput();
                    } else {
                        enterProcessor.run();
                    }
                    break;
                case BACK_SPACE:
                    if (!getUserInput().isEmpty()) {
                        userInput.setLength(userInput.length() - 1);
                    }
                    break;
                case TAB:
                    String ch = hint.apply(getUserInput());
                    resetUserInput();
                    userInput.append(ch);
                    break;
                case SPACE:
                    userInput.append(" ");
                    break;
                default:
                    keyProcessor.accept(key);
                    break;
            }
        }
    }
}
