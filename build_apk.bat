@echo off
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME: %JAVA_HOME%
java -version
call gradlew.bat assembleDebug
echo.
echo ===========================
echo APK Build Complete!
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo ===========================
