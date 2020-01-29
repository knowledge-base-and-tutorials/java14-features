#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
source $SCRIPT_DIR/../environment
$JAVA_HOME/bin/java -Dnashorn.args=--language=es6 --enable-preview -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.tb.TextBlocks