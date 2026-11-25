# Windows 0.2.1 — startup recovery build

This build responds to the reported blank Windows window. Its exact cause on the affected laptop is still unconfirmed. This is a recovery and diagnostic build, not a verified fix on that device.

Changes:
- Preserves the separately requested 10 MiB engram JSON and 1,000,000-character biography limits. Storage limits do not enlarge the model context window.
- Software rendering is enabled before Electron starts. The interface no longer depends on hardware-accelerated rendering. Local model inference remains CPU-only.
- The interface loads from an explicit packaged file path. Missing page, preload failure, renderer crash and failure to complete startup produce a native error message and diagnostic code.
- A static opening panel remains visible while the app connects. JavaScript errors, missing preload bridge, saved-state request failure and initial rendering errors display recovery instructions.
- Native View and Help menus work independently of the page. View → Reload interface retries loading; Help → Save startup diagnostics exports a report with version, platform and startup event codes. It does not export profile text, chats, model paths, API keys or calendar tokens.
- Older partial settings merge with defaults before rendering. Invalid saved data causes an explicit error and is not overwritten.

Close Engram Daily completely using its tray Quit command, then install this update. Start it without selecting or loading a model: the Today page must appear first. If the page is still blank, use Help → Save startup diagnostics and attach that JSON together with a screenshot. The report is also written automatically under the existing app-data directory as startup-diagnostics.json.

Validation: 32 Node tests and DOM checks of all six screens plus missing-bridge, failed-state and render-error recovery paths. These are not native Windows tests. A full Electron Linux launch was attempted but crashed in this execution container; native startup remains unverified. No claim is made that the laptop-specific fault has been reproduced.

This release contains the Windows 0.2.0 chat/GGUF/calendar features plus the startup changes above. The unfinished context-budgeting/cancellation/retry prototype was not included after the blank-window report changed the priority. Android is unchanged. Calendar sign-in still requires OAuth app credentials and a real-account test. Installer signing and native notification tests remain outstanding.
