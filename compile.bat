@echo off
echo Compilation du projet Java...

REM Créer le répertoire bin s'il n'existe pas
if not exist "bin" mkdir bin

REM Compiler tous les fichiers Java avec la nouvelle architecture
C:\Java\jdk-24.0.2\bin\javac -d bin -cp src/main/java src/main/java/com/example/app/*.java src/main/java/com/example/app/model/*.java src/main/java/com/example/app/controller/*.java src/main/java/com/example/app/view/*.java src/main/java/com/example/app/commands/*.java src/main/java/com/example/app/tools/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation réussie !
) else (
    echo Erreur de compilation !
    echo Vérifiez que Java est installé et que JAVA_HOME est configuré
    pause
    exit /b 1
)
