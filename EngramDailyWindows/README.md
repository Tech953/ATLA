# Engram Daily Windows 0.2.1

Read STARTUP-REPAIR.md before testing. This package preserves the newer engram-import limits.

# Engram Daily — Jade for Windows 0.2.0

A local-first career companion for any user-selected career. This release repairs GGUF validation and chat diagnostics, strengthens the saved engram prompt, and adds read-only Google/Microsoft default-calendar sign-in.

## Install and run on the supplied ROG laptop

Close the old app, including its tray process, then run Engram-Daily-Jade-Setup-0.2.0-x64.exe. Install over the existing installation to retain local data. The installer is unsigned and has not been executed on a Windows device in this validation environment.

The initial target is the supplied i7-9750H / 16 GB RAM / GTX 1650 4 GB machine. The bundled engine runs on the CPU; GPU acceleration is not enabled.

1. Download the complete model `qwen2.5-3b-instruct-q4_k_m.gguf` from the official Qwen repository:
   https://huggingface.co/Qwen/Qwen2.5-3B-Instruct-GGUF/blob/main/qwen2.5-3b-instruct-q4_k_m.gguf
2. Keep that approximately 2.1 GB file in a permanent folder. In Settings, choose Select GGUF file.
3. Set Engine to GGUF and click Save engine settings.
4. Click Test saved engine and engram. If Windows reports missing runtime components, use Install Microsoft runtime prerequisite and try again.
5. Open Engram and send a message. Save changes to the bio/name before chatting. Enter a career in Profile before generating a digest.

Do not select an `mmproj` file: it is a multimedia projector, not a standalone chat model. Gemma 4 and Qwen 3.6 architectures are not supported by this bundled b5046 runtime. The 35B Qwen model is not the target for this 16 GB configuration. Weights are downloaded separately and are not included in the installer.

## Changes

- Reads GGUF architecture metadata on selection and before inference. Rejects projectors, truncated metadata and unsupported architectures with clear explanations.
- Provides a saved-engine/engram test and displays chat failures beside the reply.
- Captures bounded local-engine startup diagnostics instead of discarding them.
- Uses an explicit character name and biography in the system prompt. Recent unsaved turns remain in RAM until app exit or history deletion; saved history is opt-in.
- Local output is capped at 384 tokens with a visible continuation notice. CPU generation can take time; the local request timeout is ten minutes. Oversized prompts still require shortening.
- Adds Google Desktop OAuth with PKCE and Microsoft device-code sign-in, encrypted credentials, manual refresh and local disconnect. See CALENDAR-SIGN-IN.md or the setup guide button in Calendar.

## Calendar scope

Sign-in requires registered OAuth app credentials. Only each provider's default calendar is retrieved, covering the prior 7 and next 90 days. This is a manually refreshed read-only snapshot, not continuous synchronization or writeback. Secondary calendars can still be imported as ICS. No invitations are sent. Notifications use the last imported/refreshed data while the app runs.

## Validation

25 automated tests; six-screen headless renderer checks; real CPU inference with Qwen2.5-3B-Instruct Q4_K_M on Linux llama.cpp b5046; verified model SHA256. The persona test identified Jade and Larcen, session context recalled Moon Garden, and a digest was generated. See INFERENCE-VALIDATION.json, UI-VALIDATION.json and VALIDATION.md.

These results do not verify the Windows binary on your laptop, native Windows notifications, live Google/Microsoft consent or all model behavior. Android remains at the prior source checkpoint and its APK has not been updated by this Windows release.

## Build

Node/npm, NSIS 3 and the pinned npm dependencies are required. Run `npm ci` then `npm run dist`. Set `MAKENSIS` if it is not on PATH. `ENGRAM_ELECTRON_DIST` can supply an already verified unpacked Windows Electron distribution. Output is under dist/. Models remain external to the installer.

## Remaining limits

No general analytics engine, outcome prediction, neuroplasticity measurement, multimedia input, GPU offload, Responses API adapter, automatic provider writeback or background ChatGPT execution. Planner mode is a deterministic digest template, not a chat model. Local profile, calendar snapshots and history are ordinary app files; API keys and calendar credentials use OS encryption. Model-generated content may be mistaken.
