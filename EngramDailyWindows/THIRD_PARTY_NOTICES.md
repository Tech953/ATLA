# Third-party notices

- Electron 44.4.1: MIT; Electron/Chromium notices are included in the packaged distribution (`LICENSE.electron.txt`, `LICENSES.chromium.html`).
- llama.cpp b5046: MIT; upstream license is in `runtime/LLAMA_LICENSE`, with archive origin/checksum in `runtime/provenance.json`. The bundled executable and DLLs are the official no-AVX x64 release. https://github.com/ggml-org/llama.cpp/releases/tag/b5046
- ical.js 2.2.1: Mozilla Public License 2.0. Source and license are included in the npm dependency tree inside the packaged app archive; upstream source: https://github.com/kewisch/ical.js
- electron-builder and build-time dependencies retain their npm licenses; `package-lock.json` records exact resolutions.
- Microsoft Visual C++ Redistributable x64 is bundled unchanged as a prerequisite installer from https://aka.ms/vc14/vc_redist.x64.exe. Its installer presents Microsoft's terms. This project does not relicense it.
- NSIS generates the setup/uninstall executables; its notices are available at https://nsis.sourceforge.io/License.

Jade and The Long Tar remain the supplied author's material. No rights to redistribute the author's unpublished character/lore to third parties are implied by the software dependencies' licenses. No GGUF model weights are included.
