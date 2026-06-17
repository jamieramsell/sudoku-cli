# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Overview

`sudoku-cli` is the command-line front-end for the [`sudoku`](https://github.com/jamieramsell/sudoku) engine — the **view** and **controller** layers of an MVC stack whose **model** is that separate engine library. The engine (grid model, solver, generator) is consumed as a dependency, not vendored; this repo should only use its public API.

The CLI source lives under the `sudoku.cli` package. The engine's own classes are in the `sudoku` (and `sudoku.generation*`) packages, supplied by the dependency.

## Engine dependency (JitPack)

The engine is resolved via **JitPack**, which builds the public engine repo from a git tag — no authentication, token, or local install required. In `pom.xml`:

- `<repositories>` declares `https://jitpack.io`.
- The dependency is `com.github.jamieramsell:sudoku:v<tag>` (currently `v1.0.2`).

To move to a newer engine release, simply bump the `<version>` here to the new tag.

## Build & Test Commands

This is a Maven project (`pom.xml`, coordinates `io.github.jamieramsell:sudoku-cli`) following the standard layout: production source in `src/main/java/`, tests in `src/test/java/` (mirroring the `sudoku.cli` package structure). Maven build output goes to `target/`. Use the Maven Wrapper (`./mvnw`) so no global Maven install is required; the engine is resolved from JitPack and JUnit from Maven Central.

**Compile, test, lint, and package (builds `target/sudoku-cli-<version>.jar`):**
```bash
./mvnw clean verify
```

**Run all tests:**
```bash
./mvnw test
```

**Run a single test class:**
```bash
./mvnw test -Dtest=<TestClassName>
```

**Run Checkstyle only:**
```bash
./mvnw checkstyle:check
```

**Run the CLI:**
```bash
./mvnw -q compile
java -cp target/classes sudoku.cli.Main
```
(A self-contained runnable jar via `maven-shade-plugin` is planned but not yet configured; until then, run from the compiled classes as above.)

## Code Style

- Follow the **Google Java Style Guide** (2-space indent, 100-char lines, `{}` on all blocks, ordered imports). This is enforced during `verify` via the bundled `google_checks.xml` and the Checkstyle plugin — note this differs from the engine repo, which uses a custom snake_case convention.
- Prefer package-private visibility where public access isn't required.
- Javadoc on all public methods. Inline comments used to explain non-obvious logic blocks.
