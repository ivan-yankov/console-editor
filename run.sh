tput civis
java -cp target/console-utils-assembly-latest.jar yankov.console.ConsoleTableEditorMain $(tput lines) $(tput cols) $@
tput cnorm
