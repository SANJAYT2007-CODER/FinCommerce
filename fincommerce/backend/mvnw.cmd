@echo off
setlocal

set MAVEN_DIR=%~dp0apache-maven-3.9.6\bin

if exist "%MAVEN_DIR%\mvn.cmd" (
    echo [FinCommerce] Using local Maven at %MAVEN_DIR%\mvn.cmd...
    "%MAVEN_DIR%\mvn.cmd" %*
) else (
    echo [FinCommerce] Local Maven not found at %MAVEN_DIR%\mvn.cmd. Trying system mvn...
    mvn %*
)
