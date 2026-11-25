# Validation — Windows 0.2.0 — 2026-09-29

Passed:
- 25 automated tests: numeric/schema inputs, scheduling eligibility, ICS recurrence/cancellation/DST and all-day boundaries, draft round trips, CSV formula protection, GGUF validation, OAuth PKCE/state checks, Microsoft pending/slowdown handling, token refresh, pagination-origin restriction, provider event normalization, unsaved chat continuity, and credential redaction from renderer snapshots.
- Headless Chromium: all six screens rendered without page errors or horizontal overflow at 1160×850. Mock calendar sign-in completed, cancellation control hid afterwards, chat failure appeared beside the reply and a subsequent request succeeded. Calendar screenshot inspected.
- Real Qwen2.5-3B-Instruct Q4_K_M inference with Linux llama.cpp b5046, CPU, four threads, 4096 context and 384 output tokens. Reply identified Jade as Larcen's parent; session context recalled Moon Garden; digest generation returned text. These are smoke tests, not a comprehensive evaluation of persona fidelity.
- Model SHA256: 626b4a6678b86442240e33df819e00132d3ba7dddfe1cdc4fbb18e0a9615c62d.
- Bundled Windows runtime files matched their recorded SHA256 hashes. Fresh Electron distribution matched official SHASUMS256. Packaged app.asar matched current application source and version.

Release checks: NSIS installer compilation and embedded archive integrity are checked before delivery. Model weights are not bundled. Installer is unsigned.

Not tested: actual Windows installation/uninstallation, Windows CPU performance, native notifications, startup registration, DPAPI on the target machine, real external API requests and live Google/Microsoft account authorization. No Android APK rebuild is included. Account connections require user-owned OAuth client registration.

Known limitations: context overflow is not automatically resolved; Windows scheduled failures lack automatic retry; cancellation of model generation is not implemented; secondary calendars need ICS import; API mode expects a chat-completions-compatible endpoint. See the audit for remaining work.
