@echo off
REM RICB Backend Application - Build and Run Script
REM This script builds and runs the Spring Boot application

echo.
echo ============================================
echo RICB Backend - Spring Boot 3.0 Build & Run
echo ============================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 and set JAVA_HOME environment variable
    pause
    exit /b 1
)

REM Display Java version
echo Java Version:
java -version
echo.

REM Get the directory where this script is located
cd /d "%~dp0"
echo Working Directory: %cd%
echo.

REM Menu
:menu
echo Options:
echo 1. Clean and Build (download dependencies)
echo 2. Build only (use cached dependencies)
echo 3. Build and Run
echo 4. Run only (uses previously built JAR)
echo 5. Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto clean_build
if "%choice%"=="2" goto build
if "%choice%"=="3" goto build_and_run
if "%choice%"=="4" goto run_only
if "%choice%"=="5" goto exit_script
goto invalid_choice

:clean_build
echo.
echo Cleaning and building the project...
echo This may take 2-5 minutes on first build (downloading dependencies)
echo.
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo.
    echo ERROR: Build failed! Check the error messages above.
    pause
    goto menu
)
echo.
echo BUILD SUCCESSFUL!
pause
goto menu

:build
echo.
echo Building the project (using cached dependencies)...
echo.
call mvnw.cmd package -DskipTests
if errorlevel 1 (
    echo.
    echo ERROR: Build failed! Check the error messages above.
    pause
    goto menu
)
echo.
echo BUILD SUCCESSFUL!
pause
goto menu

:build_and_run
echo.
echo Building the project...
echo.
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo.
    echo ERROR: Build failed! Check the error messages above.
    pause
    goto menu
)
echo.
echo BUILD SUCCESSFUL! Starting application...
echo.
call mvnw.cmd spring-boot:run
pause
goto menu

:run_only
echo.
echo Checking if JAR file exists...
if not exist "target\ricb_api-0.0.1-SNAPSHOT.jar" (
    echo.
    echo ERROR: JAR file not found. Please build first (option 1 or 2)
    pause
    goto menu
)
echo.
echo Starting application...
echo Application will run at: http://localhost:8080
echo Press Ctrl+C to stop the application
echo.
java -jar target/ricb_api-0.0.1-SNAPSHOT.jar
pause
goto menu

:invalid_choice
echo.
echo Invalid choice! Please enter 1, 2, 3, 4, or 5
echo.
pause
goto menu

:exit_script
echo.
echo Exiting...
exit /b 0

