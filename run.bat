@echo off
echo Exécution du projet Java...

REM Vérifier si les classes sont compilées
if not exist "bin\com\example\app\Main.class" (
    echo Les classes ne sont pas compilées. Compilation en cours...
    call compile.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Échec de la compilation !
        pause
        exit /b 1
    )
)

REM Exécuter l'application
C:\Java\jdk-24.0.2\bin\java -cp "bin;lib/*" com.example.app.Main

pause
