#!/bin/bash
source environment
MAVEN_OPTS=-Xmx2048M
$MAVEN_HOME/bin/mvn "$@"
