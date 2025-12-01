#!/usr/bin/env bash
set -euo pipefail

# Minimal wrapper: delegate to system 'gradle' if available.
# This keeps builds reproducible when Gradle is installed on developer machines.
# If you prefer an offline wrapper that downloads a Gradle distribution,
# add the Gradle Wrapper JAR into gradle/wrapper/ and regenerate the wrapper.

DIR="$(cd "$(dirname "$0")" && pwd)"
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle CLI not found. Install Gradle or run this project in Android Studio."
  echo "Or install the Gradle Wrapper by running 'gradle wrapper' on a machine with Gradle."
  exit 1
fi
