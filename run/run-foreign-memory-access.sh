#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
source $SCRIPT_DIR/../environment
$JAVA_HOME/bin/java --enable-preview --add-modules=jdk.incubator.foreign -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.fma.SleepAnalytics
#$JAVA_HOME/bin/java --enable-preview --add-modules=jdk.incubator.foreign -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.fma.ForeignMemoryAccessExamples
#$JAVA_HOME/bin/java -Dcom.sun.management.jmxremote.port=9000 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:MaxDirectMemorySize=12G --enable-preview --add-modules=jdk.incubator.foreign -cp $SCRIPT_DIR/../target/classes com.github.kbnt.java14.fma.ByteBufferExample