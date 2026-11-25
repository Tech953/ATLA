# Validation record — updated after actual APK build

## Completed

- Successfully built `:app:assembleDebug` with Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17, Android SDK 35, Build Tools 34.0.0, NDK 27.0.12077973 and CMake 3.22.1.
- Both arm64-v8a and x86_64 native GGUF libraries were included in the APK.
- `apksigner verify --verbose` passed using APK Signature Scheme v2 (one signer).
- AAPT confirmed package `dev.engram.daily`, version 0.1.0/code 1, minimum Android 9/API 28 and target API 35, with the expected launch activity.
- Core scenario arithmetic/input tests passed. XML/JSON and archive checks passed.

The separately supplied `Engram-Daily-Jade-0.1.0-test.apk` is an actual installable, debug-signed test APK, not a source archive. It is not a production release signed with the user's own release keystore.

## Still requires device verification

No emulator or physical Android device was used. UI behavior, permissions, scheduled notifications, Keystore integration, API connectivity, GGUF inference performance, memory pressure, activity recreation and model compatibility have not been tested on-device. Run those checks before release distribution. The optional GitHub Actions workflow has not been executed.

For production updates, generate a release APK with your own retained keystore and increment the version code. A production APK signed with a different key cannot update this debug-signed installation in place.
