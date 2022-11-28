tput civis
java -cp target/console-utils-latest-jar-with-dependencies.jar:lib/raw-console-input-latest-jar-with-dependencies.jar yankov.console.ConsoleTableEditorMain $(tput lines) $(tput cols) $@
tput cnorm
