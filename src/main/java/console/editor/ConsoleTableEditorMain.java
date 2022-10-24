package console.editor;

import console.util.TableParser;
import console.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleTableEditorMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            Utils.logError("Missing required argument: csv-file");
            Utils.logError("CSV file with at least header line is expected.");
            return;
        }

        Path csvFile = Paths.get(args[0]);
        String csv = Utils.readFile(csvFile).orElse("");
        ConsoleTableEditor editor = new ConsoleTableEditor();
        Table table = TableParser.fromCsv(csv);
        editor.edit(table);
        System.out.println();
        System.exit(0);
    }
}
