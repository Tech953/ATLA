# Engram Daily functionality audit — Windows 0.2.0

Updated 2026-09-29. This is a verified build with bounded smoke tests, not a claim that every requested feature is complete.

## Root cause and repaired behavior

The supplied Gemma and Qwen files were `mmproj` multimedia projectors, which cannot independently generate chat. The old importer checked only the GGUF magic bytes. Both source projects now inspect architecture metadata; the new Windows installer includes that repair. It rejects projectors and unsupported architectures rather than implying successful model loading.

The bundled llama.cpp b5046 does not support the requested Gemma 4 or Qwen 3.6 families. Instead, the Windows release targets the official Qwen2.5-3B-Instruct Q4_K_M, approximately 2.1 GB, suitable as an initial CPU target for the supplied i7-9750H / 16 GB RAM laptop. The GTX 1650 is not used by this build. Actual laptop performance remains to be measured.

## Current component status

| Component | Status |
|---|---|
| Windows installer | Rebuilt as 0.2.0; unsigned. Packaging integrity checked before delivery; not executed on Windows here. |
| GGUF model import | Metadata guards and engine-test control included. Metadata validation alone does not certify tensors, RAM fit or inference. |
| Real inference | Completed on Linux using the same llama.cpp release and selected model. Jade identity/lore question, conversation context and digest smoke tests passed. |
| Engram | Saved name and biography explicitly enter the system prompt. No guarantee of perfect characterization; model responses still require judgment. |
| Chat | Visible error feedback, startup diagnostics and unsaved session context repaired in Windows. Last six session turns are retained for prompts. |
| CPU constraints | Four threads, 4096 context, 384 output tokens; continuation notice on output cap. Ten-minute request timeout. No streaming, generation cancel or GPU acceleration. |
| API | Existing consent-gated HTTPS chat-completions adapter; no Responses API adapter and no real credential-backed test. |
| Google calendar | Desktop OAuth PKCE, encrypted tokens, manual default-calendar refresh, local disconnect. Registered client JSON required. |
| Microsoft calendar | Public-client device-code flow, encrypted tokens, manual default-calendar refresh, local disconnect. Registered client ID required. |
| Calendar accuracy | Provider recurrence instances, cancellation handling, bounded pagination and Graph all-day original-zone fetch. Protocol and synthetic event tests passed; real account flow unverified. |
| Calendar boundaries | Read-only default calendars; prior 7/next 90 days. No account selection for multiple accounts per provider, secondary-calendar picker, automatic polling or writeback. ICS remains supported. |
| Reminders/digest | Implemented existing local scheduling paths; native Windows delivery remains untested. App must be running. Failed daily attempts lack automatic retry. |
| Analytics | Practice logs and arithmetic effort scenarios only; no general analytics/simulation engine or neural-plasticity measurement. |
| Privacy | Engine key and OAuth configuration/tokens use Windows encryption; secrets omitted from renderer snapshots and exports. Profile/calendar/history remain ordinary local files. |
| Export/restore | Exports are not full backups; complete calendar/settings restoration is not implemented. |
| Android | Prior source metadata-validation/engine-test fixes retained. APK is unchanged; no calendar OAuth or Windows 0.2.0 feature parity is claimed. |
| ChatGPT variant | Prompt/packet workflow only. It cannot itself install GGUF inference or create background notifications. |

## Evidence

25 automated tests pass. Six-screen renderer tests pass with mocked IPC. Real model results are recorded in the Windows source INFERENCE-VALIDATION.json. Installer source contents and runtime hashes were checked. These tests do not replace device testing or live account consent.

## Outstanding acceptance gates

1. Install Windows 0.2.0 on the supplied laptop, select the full Qwen model, test Jade, chat twice, restart and verify persistent versus unsaved history behavior.
2. Verify native reminders, tray behavior, startup registration and uninstall/reinstall on Windows.
3. Register Google/Microsoft OAuth clients and test real consent, refresh, disconnect and revoked permission behavior against known calendar events.
4. Complete Android feature parity and rebuild with a preserved signing key; test on the actual Android target.
5. Address context budgeting, generation cancellation, API dialect coverage, scheduled retry and full backup/restore before a production release.

The comprehensive original vision remains broader than this release. This update provides a concrete Windows chat/model repair and calendar connection implementation, with the unverified parts identified explicitly.
