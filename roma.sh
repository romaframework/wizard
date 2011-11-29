#!/bin/bash

if [ -z "$JAVA_HOME" ]
then
	JAVA=java
else
	JAVA=$JAVA_HOME/bin/java
fi


if [ "$1" == "home" ] || [ -z "$ROMA_HOME" ]
then
	PRG="$0"

	# need this for relative symlinks
	while [ -h "$PRG" ]
	do
	   PRG=`readlink "$PRG"`
	done

	ROMA_HOME=`dirname "$PRG"`

	if [ "$ROMA_HOME" == "." ]
	then 
		ROMA_HOME=`pwd` 
	fi

	if [ "$1" == "home" ]
	then
		echo $ROMA_HOME
	else
		export ROMA_HOME=$ROMA_HOME
	fi
fi

if [ "$1" != "home" ]
then 
	$JAVA -cp "$ROMA_HOME/lib/xmltask.jar:$ROMA_HOME/lib/ant-contrib.jar:$ROMA_HOME/lib/ant.jar:$ROMA_HOME/lib/ant-launcher.jar:$ROMA_HOME/lib/ant-nodeps.jar:$ROMA_HOME/lib/commons-io.jar:$ROMA_HOME/lib/commons-jxpath.jar:$ROMA_HOME/lib/commons-logging.jar:$ROMA_HOME/lib/files:$ROMA_HOME/lib/ivy.jar:$ROMA_HOME/lib/log4j.jar:$ROMA_HOME/lib/roma-console.jar:$ROMA_HOME/lib/roma-core.jar:$ROMA_HOME/lib/spring-asm.jar:$ROMA_HOME/lib/spring-beans.jar:$ROMA_HOME/lib/spring-context.jar:$ROMA_HOME/lib/spring-core.jar:$ROMA_HOME/lib/spring-expression.jar:$ROMA_HOME/dist/roma-wizard.jar" org.romaframework.console.RomaMain $@
fi
