
@set STARTER_CP=lib/groovy-all-1.6.2.jar
@set STARTER_MAIN_CLASS=org.codehaus.groovy.tools.GroovyStarter
@set STARTER_CONF=%GROOVY_HOME%\conf\groovy-starter.conf

@set JAVA_EXE=java
@rem @set JAVA_OPTS=-Xmx128m  -Dprogram.name="%PROGNAME%"  -Dgroovy.home="%GROOVY_HOME%" -Dtools.jar="%TOOLS_JAR%" -Dgroovy.starter.conf="%STARTER_CONF%" -Dscript.name="%GROOVY_SCRIPT_NAME%"
@set JAVA_OPTS=-Xmx128m  -Dprogram.name="caas"  -Dgroovy.home="." -Dtools.jar="c:\Data\apps\java\jdk1.6.0_04\lib\tools.jar" 

@set CLASS=org.codehaus.groovy.tools.shell.Main
@set CP=build/classes/
@set CP=%CP%;lib/groovy-all-1.6.5.jar
@set CP=%CP%;lib/log4j-1.2.13.jar
@set CP=%CP%;lib/commons-httpclient-3.1.jar
@set CP=%CP%;lib/commons-logging-1.0.4.jar
@set CP=%CP%;lib/commons-codec-1.2.jar
@set CP=%CP%;lib/jdom-1.0.jar
@set CP=%CP%;lib/jline-0.9.94.jar
@set CP=%CP%;lib/commons-cli-1.2.jar


@set CMD_LINE_ARGS=%1 %2 %3 %4 %5 %6 %7 %9 %9

@rem %JAVA_EXE% %JAVA_OPTS% -classpath "%STARTER_CP%" %STARTER_MAIN_CLASS% --main %CLASS% --conf "%STARTER_CONF%" --classpath "%CP%" %CMD_LINE_ARGS%
%JAVA_EXE% -Dscript.name=init.groovy %JAVA_OPTS% -classpath "%CP%" %STARTER_MAIN_CLASS% --main %CLASS% --classpath %CP% %CMD_LINE_ARGS%

@set CP=%CP%;lib/all/ant-1.7.1.jar
@set CP=%CP%;lib/all/ant-junit-1.7.1.jar
@set CP=%CP%;lib/all/commons-logging-1.1.jar
@set CP=%CP%;lib/all/ant-launcher-1.7.1.jar
@set CP=%CP%;lib/all/antlr-2.7.7.jar
@set CP=%CP%;lib/all/ivy-2.0.0.jar
@set CP=%CP%;lib/all/asm-2.2.3.jar
@set CP=%CP%;lib/all/asm-analysis-2.2.3.jar
@set CP=%CP%;lib/all/jsp-api-2.0.jar
@set CP=%CP%;lib/all/asm-tree-2.2.3.jar
@set CP=%CP%;lib/all/junit-3.8.2.jar
@set CP=%CP%;lib/all/asm-util-2.2.3.jar
@set CP=%CP%;lib/all/servlet-api-2.4.ja
@set CP=%CP%;lib/all/bsf-2.4.0.jar
@set CP=%CP%;lib/all/xstream-1.3.jar
