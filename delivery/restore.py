"""Reassemble the original Engram Daily files supplied for this upload."""

import argparse
import hashlib
import json
import os
from pathlib import Path
from tempfile import NamedTemporaryFile


ROOT = Path(__file__).resolve().parent.parent


def sha256(path):
    with path.open("rb") as source:
        return hashlib.file_digest(source, "sha256").hexdigest()


def restore(item):
    target = ROOT / item["path"]
    if target.exists():
        if target.stat().st_size != item["size"] or sha256(target) != item["sha256"]:
            raise ValueError(f"Existing file differs from the original: {item['path']}")
        print(f"Verified {item['path']}")
        return

    target.parent.mkdir(parents=True, exist_ok=True)
    with NamedTemporaryFile(dir=target.parent, delete=False) as output:
        temp = Path(output.name)
        try:
            digest = hashlib.sha256()
            for relative in item["parts"]:
                with (ROOT / relative).open("rb") as part:
                    while block := part.read(1024 * 1024):
                        digest.update(block)
                        output.write(block)
            if output.tell() != item["size"] or digest.hexdigest() != item["sha256"]:
                raise ValueError(f"Reassembled file failed its integrity check: {item['path']}")
            output.close()
            os.replace(temp, target)
        finally:
            output.close()
            temp.unlink(missing_ok=True)
    print(f"Restored {item['path']}")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("filename", nargs="?", help="Restore only this filename")
    args = parser.parse_args()
    items = json.loads((ROOT / "delivery" / "manifest.json").read_text(encoding="utf-8"))
    if args.filename:
        items = [item for item in items if Path(item["path"]).name == args.filename]
        if not items:
            parser.error("The filename is absent from the delivery manifest.")
    for item in items:
        restore(item)


if __name__ == "__main__":
    main()
