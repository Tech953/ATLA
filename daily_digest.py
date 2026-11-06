from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from typing import Callable, Iterable, Sequence


@dataclass(frozen=True)
class DigestEntry:
    title: str
    summary: str
    url: str | None = None


class CustomEngramDailyDigest:
    def __init__(self, engram_name: str) -> None:
        cleaned_name = engram_name.strip()
        if not cleaned_name:
            raise ValueError("engram_name must not be empty")
        self.engram_name = cleaned_name

    def build(self, entries: Iterable[DigestEntry], digest_date: date | None = None) -> str:
        digest_date = digest_date or date.today()
        lines = [
            f"# Daily Digest - {digest_date.isoformat()}",
            "",
            f"_Provided by custom engram: {self.engram_name}_",
            "",
        ]

        materialized_entries = list(entries)
        if not materialized_entries:
            lines.append("- No updates were available today.")
            return "\n".join(lines)

        for entry in materialized_entries:
            title = entry.title.strip() or "Untitled update"
            summary = entry.summary.strip()
            if entry.url:
                lines.append(f"- **{title}**: {summary} ({entry.url})")
            else:
                lines.append(f"- **{title}**: {summary}")

        return "\n".join(lines)

    def disseminate(
        self,
        digest: str,
        recipients: Sequence[str],
        send: Callable[[str, str, str], None],
    ) -> tuple[str, ...]:
        normalized_recipients = tuple(
            dict.fromkeys(recipient.strip() for recipient in recipients if recipient.strip())
        )
        subject = f"Daily Digest ({self.engram_name})"

        for recipient in normalized_recipients:
            send(recipient, subject, digest)

        return normalized_recipients
