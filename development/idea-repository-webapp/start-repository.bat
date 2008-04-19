@echo off

set CATALINA_HOME=M:\applicationServer\apache-tomcat-6.0.10

REM --------------------------------------------
REM ----- Java options for Repository project
REM --------------------------------------------
set SEC_PATH=M:/JavaProjects/IDEABase/IDEAContentRepo/development/idea-repository-common/src/main/resources/security
set JAVA_OPTS=%JAVA_OPTS% -Djava.security.auth.login.config=%SEC_PATH%/jaas.config
set JAVA_OPTS=%JAVA_OPTS% -Djava.security.auth.policy=%SEC_PATH%/repository.policy

echo %JAVA_OPTS%
echo Start tomcat server.
M:\applicationServer\apache-tomcat-6.0.10\bin\catalina run