# Engram JSON import limits

Windows source update 30 September 2026. Engram JSON and Android profile-transfer JSON now allow files up to 10 MiB (10,485,760 bytes), previously 2,000,000 bytes. Biography storage/validation increases from 6,000 to 1,000,000 JavaScript string units. Name, profile, and notes limits remain unchanged. Imported text is preserved rather than silently truncated. Schema version 1 with name and bio is still required for engram files.

Three focused Node tests pass, covering round-trip fidelity and exact validation boundaries. This is a source update only: the distributed Windows installer and Android APK are unchanged, and this patch does not alter Android import behavior. The saved biography limit does not enlarge the GGUF/API context window; oversized inference requests may still require context selection. No large-biography model inference is claimed.
