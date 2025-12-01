@echo off
REM Minimal wrapper: delegate to system 'gradle' if available.
where gradle >nul 2>nul
IF %ERRORLEVEL%==0 (
  gradle %*
) ELSE (
  echo Gradle CLI not found. Install Gradle or run this project in Android Studio.
  echo Or run 'gradle wrapper' on a machine with Gradle to produce a proper wrapper.
  exit /b 1
)
