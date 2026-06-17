---
name: Breaking Change
about: A change that breaks an existing interface, public API, or established behaviour
labels: breaking
---

## Summary
<!-- One or two sentences. What needs to change, and why can it not be done compatibly? -->

## What Breaks
<!-- Describe the current interface / behaviour that will change. Reference the
     affected type(s), method signature(s), or contract. -->

## Proposed Change
<!-- Describe the new interface / behaviour. Show before/after signatures if useful. -->

## Justification
<!-- Why is a breaking change necessary rather than a backward-compatible one?
     A breaking change must be justified before it is approved. -->

## Affected Components
<!-- Which packages, classes, or interfaces are impacted? List anything downstream
     that depends on the current behaviour. -->

## Impact & Migration
<!-- Who/what is affected, and how should callers migrate? Note any version bump
     implied under Semantic Versioning. -->

## Milestone
<!-- Which milestone (if any) does this work towards? e.g. M1, or v1-0-0 -->

## Acceptance Criteria
<!-- What must be true for this issue to be closed? Write these as checkboxes. -->
- [ ] The breaking change is implemented and reviewed
- [ ] The commit introducing the break is flagged with `!` (e.g. `feat!:`, `refactor!:`)
- [ ] Affected interfaces, docs, and the CHANGELOG are updated
- [ ] 
