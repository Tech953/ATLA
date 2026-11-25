"""Copy the verified source, archives, and documentation into a clean checkout."""

import hashlib
import json
from pathlib import Path
import shutil
import sys


root = Path(__file__).resolve().parents[1]
target = Path(sys.argv[1]).resolve()
files = json.loads((root / "delivery" / "project-files.json").read_text(encoding="utf-8"))


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


for name, expected in files.items():
    source = root / name
    dest = target / name
    if digest(source) != expected:
        raise SystemExit(f"Source checksum mismatch: {name}")
    if dest.exists():
        if digest(dest) != expected:
            raise SystemExit(f"Another version is already present: {name}")
        continue
    dest.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(source, dest)

readme = target / "README.md"
section = (root / "delivery" / "download-links.md").read_bytes()
existing = readme.read_bytes()
if section not in existing:
    if b"## Engram Daily downloads" in existing:
        raise SystemExit("The repository already has a different downloads section.")
    readme.write_bytes(existing.rstrip(b"\n") + b"\n" + section)
