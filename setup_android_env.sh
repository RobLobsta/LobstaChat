#!/bin/bash

set -e
set -x

# Define constants
ANDROID_SDK_HOME="/home/jules/android-sdk"
ANDROID_CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11391160_latest.zip"
ANDROID_NDK_VERSION="27.2.12479018"
ANDROID_BUILD_TOOLS_VERSION="35.0.0"
ANDROID_PLATFORM_VERSION="34"

# Create the Android SDK directory structure
mkdir -p "$ANDROID_SDK_HOME"
mkdir -p "$ANDROID_SDK_HOME/cmdline-tools"

# Download and install Android SDK Command-line Tools
cd /tmp
wget -q "$ANDROID_CMDLINE_TOOLS_URL"
unzip -q "$(basename "$ANDROID_CMDLINE_TOOLS_URL")" -d "$ANDROID_SDK_HOME/cmdline-tools"

# Fix the directory structure as required by sdkmanager
mv "$ANDROID_SDK_HOME/cmdline-tools/cmdline-tools" "$ANDROID_SDK_HOME/cmdline-tools/latest"

# Export ANDROID_HOME and add sdkmanager to PATH
export ANDROID_HOME="$ANDROID_SDK_HOME"
export PATH="$PATH:$ANDROID_SDK_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"
SDKMANAGER="$ANDROID_SDK_HOME/cmdline-tools/latest/bin/sdkmanager"

# Accept all licenses
yes | $SDKMANAGER --licenses > /dev/null

# Install SDK packages
echo "Installing Android SDK packages..."
$SDKMANAGER "platform-tools"
$SDKMANAGER "build-tools;$ANDROID_BUILD_TOOLS_VERSION"
$SDKMANAGER "platforms;android-$ANDROID_PLATFORM_VERSION"
$SDKMANAGER "ndk;$ANDROID_NDK_VERSION"

echo "Android environment setup complete."
echo "ANDROID_HOME is set to $ANDROID_HOME"

cd -
