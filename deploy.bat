@echo off
REM =========================================
REM SnapSpace Dev Deploy Script for Tomcat 11
REM =========================================

REM Set Tomcat installation folder
set "TOMCAT_HOME=F:\apache-tomcat-11.0.15"
set "CATALINA_HOME=%TOMCAT_HOME%"

REM Build the project with Maven
echo Building SnapSpace WAR...
call mvn clean package

IF ERRORLEVEL 1 (
    echo Maven build failed! Exiting.
    pause
    exit /b 1
)

REM Copy WAR to Tomcat webapps
echo Deploying SnapSpace.war to Tomcat...
copy /Y "target\SnapSpace.war" "%TOMCAT_HOME%\webapps\SnapSpace.war"

REM Stop Tomcat
echo Stopping Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat"

REM Wait a few seconds to ensure Tomcat shuts down
timeout /t 3 /nobreak >nul

REM Start Tomcat
echo Starting Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"

echo Deployment complete! Open http://localhost:8080/SnapSpace
pause
