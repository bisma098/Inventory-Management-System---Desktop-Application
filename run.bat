@echo off
setlocal EnableDelayedExpansion

echo ========================================
echo   IMS JavaFX Application Launcher
echo ========================================

set JAVA_HOME=C:\Program Files\Java\jdk-21
set JAVAFX_PATH=lib\javafx-sdk-21.0.9\lib
set SQL_JDBC_JAR=lib\mssql-jdbc-13.2.1.jre11.jar
set SRC_DIR=src
set OUT_DIR=out

echo.
echo [1/4] Cleaning previous build...
if exist "%OUT_DIR%" rmdir "%OUT_DIR%" /S /Q 2>nul
mkdir "%OUT_DIR%"

echo.
echo [2/4] Compiling ALL Java source files...

:: Create a temporary file list
set FILE_LIST=%TEMP%\java_files.txt
dir "%SRC_DIR%\*.java" /s /b > "%FILE_LIST%"

:: Compile all Java files
"%JAVA_HOME%\bin\javac" ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.fxml ^
    -d "%OUT_DIR%" ^
    -cp "%SQL_JDBC_JAR%" ^
    "@%FILE_LIST%"

:: Clean up
del "%FILE_LIST%" 2>nul

if errorlevel 1 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Copying ALL resource files...
xcopy "%SRC_DIR%\ims\view\*.fxml" "%OUT_DIR%\ims\view\" /Y /I >nul 2>&1
xcopy "%SRC_DIR%\ims\view\*.css" "%OUT_DIR%\ims\view\" /Y /I >nul 2>&1
xcopy "%SRC_DIR%\ims\controller\*.fxml" "%OUT_DIR%\ims\controller\" /Y /I >nul 2>&1

echo.
echo [4/4] Starting application...
"%JAVA_HOME%\bin\java" ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.fxml ^
    -cp "%SQL_JDBC_JAR%;%OUT_DIR%" ^
    ims.view.Main

pause