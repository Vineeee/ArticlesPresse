@echo off
echo Compilation et exécution des tests...

REM Créer le répertoire bin s'il n'existe pas
if not exist "bin" mkdir bin

REM Compiler les classes principales
javac -d bin -cp "lib/*" src/main/java/com/example/app/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Erreur de compilation des classes principales !
    pause
    exit /b 1
)

REM Compiler les tests
javac -d bin -cp "bin;lib/*" src/test/java/com/example/app/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Erreur de compilation des tests !
    pause
    exit /b 1
)

echo Compilation des tests réussie !

REM Exécuter les tests (exemple simple)
echo Exécution des tests...
java -cp "bin;lib/*" com.example.app.MainTest

pause
