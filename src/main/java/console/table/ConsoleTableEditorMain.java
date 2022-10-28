package console.table;

import console.Const;
import console.util.TableParser;
import console.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleTableEditorMain {
    public static void main(String[] args) {
        if (args.length < 3) {
            Utils.writeError("Missing required argument. Required 3 provided " + args.length);
            Utils.writeln("Program arguments:");
            Utils.writeln(Const.TAB + "number-of-console-lines [required]: Number of lines of the console");
            Utils.writeln(Const.TAB + "number-of-console-columns [required]: Number of columns of the console");
            Utils.writeln(Const.TAB + "input-file [required]: CSV file with at least header line");
            return;
        }

        int lines = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        Path csvFile = Paths.get(args[2]);

        String csv = Utils.readFile(csvFile).orElse("");
        Table<String> table = TableParser.fromCsv(csv);
        ConsoleTableViewer<String> editor = new ConsoleTableEditor(table, csvFile, lines, columns);
        editor.show();

        Utils.writeln();
        System.exit(0);
    }
}
