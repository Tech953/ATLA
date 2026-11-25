# Engram Daily · Jade 0.2.1 — startup recovery

## Install

1. Close the old app, including its tray process (right-click the tray icon and choose Quit).
2. Run Engram-Daily-Jade-Setup-0.2.1-x64.exe.
3. Open Engram Daily. The Today screen should appear without loading a GGUF file.
4. If it remains blank, press Alt to reveal the native menu if necessary, choose Help → Save startup diagnostics, and attach the resulting JSON along with a screenshot.

This update preserves the app's existing profile location and does not delete model files. Do not erase your saved data to diagnose a blank window.

## Included repairs

- Software rendering to remove reliance on accelerated graphics initialization.
- Explicit packaged page paths and visible startup-failure reporting.
- Native View → Reload interface and Help → Save startup diagnostics controls, independent of the page.
- Startup checks for preload failure, page failure, renderer crash and incomplete initialization.
- Deep merging of older partial settings with defaults.
- Preserved 10 MiB engram JSON import limit and 1,000,000-character biography storage limit. Model context limits remain separate.

The diagnostic report contains application/runtime versions and startup event codes. It does not include chat text, biography, calendar events, model paths, API keys or OAuth tokens.

## Verification and limits

On 15 October 2026, all 32 source tests passed. DOM checks passed for all six screens and three startup-error paths. Packaged entrypoint, version and changed app files match the saved source.

This is an unsigned Windows recovery build. The exact cause of the reported blank window has not been confirmed on the affected laptop. Native Windows installation/startup, notifications and real calendar authorization remain unverified. The Android APK is unchanged. This installer is not a claim that the comprehensive functionality audit is finished.

Installer SHA-256:
981487b9e7df0fb977407debec68a2deb670a2fb66e87fafdf6ae9f21f0b76cd
