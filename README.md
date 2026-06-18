# sudoku-cli

> A command-line interface for playing, solving, and generating Sudoku puzzles — the view and controller layers built on top of the [`sudoku`](https://github.com/jamieramsell/sudoku) engine.

[![Java](https://img.shields.io/badge/java-21+-orange.svg)](https://openjdk.org)
[![Code style: Google](https://img.shields.io/badge/code%20style-Google%20Java%20Style-blue.svg)](https://google.github.io/styleguide/javaguide.html)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Status: WIP](https://img.shields.io/badge/status-in%20development-yellow.svg)](#project-status)

---

## Overview

`sudoku-cli` is the terminal front-end for the [`sudoku`](https://github.com/jamieramsell/sudoku) engine. The engine provides the **model** — grid representation, a backtracking solver, and a symmetric puzzle generator — and this project adds the **view** (rendering the grid and reading input in the terminal) and **controller** (translating player actions into engine calls), completing an MVC stack across two repositories.

The engine is consumed as a published library rather than copied in, so this repo depends only on its public API.

## Project status

> 🚧 **Early development.** The build, tooling, and engine integration are in place; interactive gameplay is being built out. See [Roadmap](#roadmap).

---

## Architecture

This project is the **V** and **C** of an MVC design; the **M** lives in the engine repo.

| Layer | Where | Responsibility |
|---|---|---|
| Model | [`sudoku`](https://github.com/jamieramsell/sudoku) engine | Grid state, solver, generator |
| View | `sudoku-cli` (this repo) | Render the grid and prompts to the terminal; read user input |
| Controller | `sudoku-cli` (this repo) | Translate player intent (new game, place value, hint, solve) into engine operations |

The engine dependency is resolved at build time via **[JitPack](https://jitpack.io)**, which builds the public engine repo from its release tag — no authentication or local install required:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.jamieramsell</groupId>
  <artifactId>sudoku</artifactId>
  <version>v1.0.2</version>
</dependency>
```

---

## Getting Started

### Prerequisites

- JDK 21+
- No Maven install needed — use the bundled Maven Wrapper (`./mvnw`). The engine and test dependencies are resolved automatically (the engine from JitPack, JUnit from Maven Central).

### Build, test, and lint

`verify` compiles the sources, runs the tests, and checks the code against the Google Java Style Guide:

```bash
./mvnw clean verify
```

### Run

```bash
./mvnw -q compile
java -cp target/classes sudoku.cli.Main
```

> A self-contained runnable jar (bundling the engine) is planned — see [Roadmap](#roadmap).

---

## Code Style

Code is checked against the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) via the bundled `google_checks.xml`, enforced by the Checkstyle plugin during `verify`. (Note: this differs from the engine repo, which uses its own snake_case convention.)

---

## Roadmap

- [x] Maven build, Google-style linting, and engine integration via JitPack
- [x] `SudokuController` — new game, place/clear cell, hint, solve, reset
- [ ] Console view — grid rendering and input loop
- [ ] Distinguish fixed clues from player-entered cells
- [ ] Runnable uber-jar (`maven-shade-plugin`) for `java -jar`
- [ ] CI workflow building against the published engine

---

## Contributing

This project uses a milestone-based branching model. Work flows from short-lived
development branches up through per-milestone stable branches into `main`:

```
<milestone>/<label>/<name>  ──PR──▶  stable-<milestone>  ──PR──▶  main
```

### Branching

- **Development branches** — `<milestone>/<label>/<name>`, where `<label>` is a
  Conventional Commit type (`feat`, `fix`, `refactor`, `docs`, `chore`, …). Version
  milestones replace dots with hyphens (`v1.0.0` → `v1-0-0`).
  - `m1/feat/icontroller` — defining the `ISudokuController` interface in milestone M1
  - `v1-0-0/refactor/consoleview` — refactoring the console view in milestone v1.0.0
- **Stable branches** — `stable-<milestone>` (e.g. `stable-m1`, `stable-v1-0-0`).
  Development branches are merged here via pull request once ready.
- **`main`** — a completed milestone is merged from its stable branch into `main`
  via a further pull request.

### Pull requests

1. Open an issue describing the change before starting work.
2. Branch from the relevant stable branch using the naming convention above.
3. Open a pull request targeting the appropriate branch (development → `stable-<milestone>`;
   completed milestone → `main`).
4. Every pull request must pass the automated checks (build, test, lint) before it can be merged.

### Commits & versioning

- **[Conventional Commits](https://www.conventionalcommits.org/)** (`feat:`, `fix:`,
  `refactor:`, `chore:`, `docs:`, …).
- **Breaking changes** are flagged with `!` (e.g. `feat!:`, `fix!:`). A breaking change
  must have a corresponding issue opened first.
- Releases follow **[Semantic Versioning](https://semver.org/)**.

---

## Related

- **[`sudoku`](https://github.com/jamieramsell/sudoku)** — the Sudoku engine (model layer) this CLI is built on.

---

## License

This project is licensed under the [MIT License](LICENSE).
