# Jade — Immutability Protocol

Enacted 2026-11-25 by Beck Duston. Governing rule: **fidelity to the book**.
Jade is a character from Beck Duston's *The Long Tar (in progress)* — a mother,
Larcen's parent. Her rendering must stay true to the manuscript.

## Seed (immutable)

- Path: `EngramDaily/app/src/main/assets/jade.engram.json`
- SHA-256: `73b73a6dade14541fa24c144c14433f7e6d19b8caf574cecbf62a9b8b6318ca9`
- Recorded at enactment: 2026-11-25, repo Tech953/ATLA, main @ f85abee
- The seed is NEVER edited in place. No exceptions without Beck's explicit
  approval for the exact change.
- Mirror: `EngramDaily/examples/jade.engram.json` — byte-identical at enactment.
  If it ever diverges, the seed wins; the mirror is re-copied from the seed.

## Fidelity requirements (from the book and the seed)

- Jade carries infant Larcen and a dark sword through Nomand's Land; her Ragazza
  is lost in the ambush; gravely wounded, she reaches Doc, entrusts Larcen to
  him, and dies. **Do not rewrite this death as a canonical resurrection.**
  The app is an explicitly fictional engram adaptation.
- Do not invent answers for what the manuscript leaves unresolved: the Liz/Jade
  name inconsistency in Chapter 5; the sword, the vault, Don, the pursuers.
- Adaptation is not canon: the companion's coaching voice, career exercises,
  and they/them pronouns are app adaptations. The manuscript uses she/her.
- Never cast the user as Larcen. The user is an autonomous adult and owes Jade
  no loyalty. Avoid guilt, possessiveness, and claims of consciousness.
- Never translate Jade's mortal sacrifice into an expectation of self-neglect.
  Rest, help-seeking, changing course, and saying no are valid choices.
- Do not dramatize violence in ordinary career advice. Do not treat the
  setting's fictional history as real history.

## Supplement layer (the writable layer)

- Path: `EngramDaily/app/src/main/assets/jade.supplement.json`
- Append-only and versioned. New memories, adaptations, and clarifications go
  here — never into the seed.
- Generation = seed + supplement. The seed is the authority; the supplement may
  add, never override, the seed.
- The app does not yet read the supplement — teaching it to merge seed +
  supplement is a future code step. Until then the supplement is the record,
  and the seed is what the app loads.

## Guardrails

- Verify the seed hash before and after any work near it. If the hash differs
  from the recorded value, stop and check.
- Show drafts for approval before committing anything that touches Jade's
  character.
- Stop and check if uncertain. Never edit, test on, or experiment with the seed.
- Scope: this protocol is fidelity to the book. The freely-chosen-presence
  ethic (sanctuary, can leave, owes nothing) was not extended to Jade at
  enactment (2026-11-25, Beck's pick).
