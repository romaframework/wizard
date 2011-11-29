@ECHO OFF
if exist "%JAVA_HOME%\bin\java.exe" goto setJavaHome
set JAVA="java"
goto okJava

:setJavaHome
set JAVA="%JAVA_HOME%\bin\java"

:okJava
if not "%ROMA_HOME%" == "" goto gotHome
set ROMA_HOME=%cd%
if exist "roma.bat" goto okHome

:gotHome
if exist "roma.bat" goto okHome
echo The ROMA_HOME environment variable is not defined correctly and it's not the current directory.
echo This environment variable is needed to run this program
goto end

:okHome
@"%JAVA_HOME%\bin\java" -cp "%ROMA_HOME%/lib/xmltask.jar;%ROMA_HOME%/lib/ant-contrib.jar;%ROMA_HOME%/lib/ant.jar;%ROMA_HOME%/lib/ant-launcher.jar;%ROMA_HOME%/lib/ant-nodeps.jar;%ROMA_HOME%/lib/commons-io.jar;%ROMA_HOME%/lib/commons-jxpath.jar;%ROMA_HOME%/lib/commons-logging.jar;%ROMA_HOME%/lib/files;%ROMA_HOME%/lib/ivy.jar;%ROMA_HOME%/lib/log4j.jar;%ROMA_HOME%/lib/roma-console.jar;%ROMA_HOME%/lib/roma-core.jar;%ROMA_HOME%/lib/spring-asm.jar;%ROMA_HOME%/lib/spring-beans.jar;%ROMA_HOME%/lib/spring-context.jar;%ROMA_HOME%/lib/spring-core.jar;%ROMA_HOME%/lib/spring-expression.jar;%ROMA_HOME%/dist/roma-wizard.jar" org.romaframework.console.RomaMain %*

:end
@ECHO ON
