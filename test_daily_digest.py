import unittest
from datetime import date

from daily_digest import CustomEngramDailyDigest, DigestEntry


class DailyDigestTests(unittest.TestCase):
    def test_build_includes_custom_engram_and_entries(self) -> None:
        digest_builder = CustomEngramDailyDigest("ATLA")
        entries = [
            DigestEntry("Signal update", "Three meaningful events", "https://example.com/event"),
            DigestEntry("", "No title should be normalized"),
        ]

        digest = digest_builder.build(entries, digest_date=date(2026, 11, 6))

        self.assertIn("# Daily Digest - 2026-11-06", digest)
        self.assertIn("_Provided by custom engram: ATLA_", digest)
        self.assertIn("- **Signal update**: Three meaningful events (https://example.com/event)", digest)
        self.assertIn("- **Untitled update**: No title should be normalized", digest)

    def test_build_without_entries_has_empty_message(self) -> None:
        digest_builder = CustomEngramDailyDigest("ATLA")

        digest = digest_builder.build([], digest_date=date(2026, 11, 6))

        self.assertIn("- No updates were available today.", digest)

    def test_disseminate_deduplicates_and_skips_blanks(self) -> None:
        digest_builder = CustomEngramDailyDigest("ATLA")
        sent = []

        def sender(recipient: str, subject: str, body: str) -> None:
            sent.append((recipient, subject, body))

        digest = "digest body"
        recipients = digest_builder.disseminate(
            digest,
            ["alpha@example.com", " ", "alpha@example.com", "beta@example.com"],
            sender,
        )

        self.assertEqual(("alpha@example.com", "beta@example.com"), recipients)
        self.assertEqual(
            [
                ("alpha@example.com", "Daily Digest (ATLA)", "digest body"),
                ("beta@example.com", "Daily Digest (ATLA)", "digest body"),
            ],
            sent,
        )


if __name__ == "__main__":
    unittest.main()
