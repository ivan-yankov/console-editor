tput civis
java -cp target/console-utils-1.0-jar-with-dependencies.jar:lib/raw-console-input-1.0-jar-with-dependencies.jar yankov.console.ConsoleTableEditorMain $(tput lines) $(tput cols) $@
tput cnorm
