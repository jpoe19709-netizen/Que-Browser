# Building Que-Browser (ViaAdvancedBrowser)

## Prerequisites
This is an **Android Application** project that requires:
- **Android SDK** (API 34 target, API 21 minimum)
- **Android Build Tools**
- **Java 8+** (currently have Java 21 ✓)
- **Gradle** (currently have Gradle 9.2.0 ✓)

## Current Environment Status
✅ Gradle 9.2.0 - installed
✅ Java 21 - installed
❌ Android SDK - **NOT installed** (required for building APK)

## To Build This Project

### Option 1: Build in Android Studio (Recommended)
1. Install Android Studio
2. Open the project directory
3. Let Android Studio install SDKs automatically
4. Click "Build > Build Bundle(s) / APK(s)"

### Option 2: Command-line Build (requires manual SDK setup)
```bash
gradle assembleDebug
gradle assembleRelease
# Use the project Gradle wrapper or system Gradle. We provide a minimal `gradlew` that
# delegates to a locally installed Gradle. If you prefer the full Gradle Wrapper that
# downloads a distribution, run `gradle wrapper` on a machine with Gradle installed.

# Set ANDROID_HOME (or ANDROID_SDK_ROOT) environment variable to your SDK path
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME

# Ensure platform tools and build tools are installed for API 34. Example using sdkmanager:
# (requires Android SDK command-line tools to be installed)
# sudo yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Build debug APK using wrapper (recommended) or system Gradle
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease
```

### Option 3: Using Docker
```bash
# Build using Android Docker image
docker run --rm -v $(pwd):/workspace -w /workspace \
  ghcr.io/androidsource/android:latest \
  gradle assembleDebug
```

### Publishing (optional)

This project registers the `com.vanniktech.maven.publish` plugin in the root `build.gradle` so you can publish artifacts from library modules (not the `app` application module). The plugin is powerful and supports publishing Android libraries (AARs) and Java/Kotlin artifacts to Maven repositories.

To use it in a library module:

1. Convert the module to a library module (`com.android.library`) if needed.
2. In the library module's `build.gradle` apply the plugin and configure publishing:

```groovy
plugins {
  id 'com.android.library'
  id 'com.vanniktech.maven.publish'
}

android {
  // library config
}

mavenPublish {
  // Example: publish to local Maven
  publishToMavenLocal = true
  // For real registries, configure Sonatype/GitHub Packages credentials here
}
```

3. Run:

```bash
./gradlew :your-library-module:publishToMavenLocal
```

See the plugin docs for advanced configuration: https://github.com/vanniktech/gradle-maven-publish-plugin


## Project Configuration
- **Target SDK**: 34
- **Min SDK**: 21
- **App ID**: com.viaadvancedbrowser
- **Version**: 1.0.0

## Build Variants
- **Debug**: Suffix `.debug`, for development/testing
- **Release**: ProGuard obfuscation enabled, optimized

## Fixed Issues
✅ Gradle configuration (buildscript → plugins)
✅ gradle.properties file naming
✅ Dependency declaration syntax
