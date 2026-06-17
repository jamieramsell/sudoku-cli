# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- Maven project (`pom.xml`) targeting Java 21, with the Maven Wrapper (`mvnw`) so no global Maven install is required.
- Integration with the [`sudoku`](https://github.com/jamieramsell/sudoku) engine, consumed via JitPack (`com.github.jamieramsell:sudoku:v1.0.2`) with no authentication required.
- Code style enforced against the Google Java Style Guide via the Checkstyle plugin (`google_checks.xml`) during `verify`.
- GitHub Actions `build` workflow running `./mvnw verify` (compile, test, and Checkstyle) on pull requests.
- Project documentation (`README.md`, `CLAUDE.md`) describing the architecture, milestone-based branching model, and contribution workflow.
- Issue templates (bug, feature, breaking change) and a pull request template.
- Initial CLI entry point (`sudoku.cli.App`).
