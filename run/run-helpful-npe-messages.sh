#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
source $SCRIPT_DIR/../environment
$JAVA_HOME/bin/java -XX:+ShowCodeDetailsInExceptionMessages --enable-preview -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.npe.HelpfulNPEMessages
#$JAVA_HOME/bin/java --enable-preview -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.npe.HelpfulNPEMessages