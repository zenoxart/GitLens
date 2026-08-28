<div align="center">

<img src="src/main/resources/icons/gitlens-128.png" width="96" height="96" alt="GitLens icon" />

# GitLens

**A JavaFX desktop app that explains *why* your code exists — not just what it does.**

[![Build](https://github.com/zenoxart/GitLens/actions/workflows/build.yml/badge.svg)](https://github.com/zenoxart/GitLens/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-9b6bf2)

### ⬇ Downloads

[**Windows**](https://github.com/zenoxart/GitLens/releases/latest/download/gitlens-windows.zip) · [**macOS**](https://github.com/zenoxart/GitLens/releases/latest/download/gitlens-macos.zip) · [**Linux**](https://github.com/zenoxart/GitLens/releases/latest/download/gitlens-linux.zip)

*Native, self-contained apps — no separate Java install needed — rebuilt from `main` on every push.*
*Unzip, then run `GitLens.exe` (Windows) · open `GitLens.app` (macOS) · run `GitLens/bin/GitLens` (Linux). Unsigned builds — Windows/macOS may warn about an unrecognized publisher on first launch.*

</div>

<br />

<div align="center">
  <img src="docs/screenshot.png" alt="GitLens screenshot" width="820" />
</div>

<br />

## Start here

Every codebase can tell you *what* a function does — the code is right there. What it can't tell you is *why* it exists: which incident caused that retry loop, which PR introduced that validation rule, which discussion settled on that timeout value. That history is scattered across commit messages, pull requests, and issue threads, and normally you go digging for it by hand.

GitLens digs for you, first — then explains.

It never lets an LLM guess at your project's history. Every question goes through a strict two-step pipeline:

```
1. RETRIEVE   Git log/diff/blame (JGit) + GitHub PRs & issues + a local Lucene index
                        ↓
2. EXPLAIN    The retrieved evidence — and only that evidence — is handed to the LLM,
              which is instructed to answer using nothing else and to cite its sources
```

Ask *"why does this retry three times?"* and GitLens doesn't hallucinate a plausible-sounding story — it finds the commit, resolves it to the pull request, resolves that to the issue that prompted it, and hands the model exactly those three things. The answer comes back with the evidence attached, and every piece of evidence is clickable: click a commit and the commit viewer opens, click a file and the source opens.

No server, no database. Your Git repository *is* the database. A hidden `.code-history/` folder in the repo just caches a Lucene index and GitHub data — delete it any time and GitLens rebuilds it from Git and GitHub alone.

## Features

| | |
|---|---|
| ✅ | Open a local repository, or clone one from a URL |
| ✅ | Repository file explorer (lazy-loaded tree) |
| ✅ | Commit history with a full diff viewer |
| ✅ | Full-text search (Apache Lucene) across commit messages and file paths |
| ✅ | AI assistant panel — evidence-backed answers with clickable citations |
| ✅ | Modern dark UI, purple accent, native window icon |
| ✅ | Settings screen for the API key, persisted to your OS's app-data folder |
| 🧩 | GitHub pull request / issue fetching — implemented, not yet wired into the UI |
| 🧩 | Java symbol extraction (JavaParser) — implemented, not yet wired into the UI |
| 🗺️ | Dependency graph view, developer-expertise scoring, method-level history |

✅ working today · 🧩 built but not surfaced in the UI yet · 🗺️ planned

## Architecture

```
                       JavaFX UI  (Repository Explorer · Commits · Diff · Search · AI Assistant)
                                        │
                     ┌──────────────────┼──────────────────┐
                     │        service/  orchestration        │
                     └──────────────────┼──────────────────┘
           ┌──────────┬──────────┬──────┴──────┬──────────┬──────────┐
           │          │          │             │          │          │
         git/      github/    search/      analysis/     ai/     storage/
        (JGit)    (GitHub    (Lucene)   (JavaParser)  (LLM call  (.code-history
                    REST)                              + prompt)   JSON cache)
```

The Java application always retrieves evidence itself before ever calling the LLM — the model explains, it never searches on its own.

## Getting started (build from source)

**Prerequisites:** JDK 21+. That's it — the Maven wrapper (`mvnw` / `mvnw.cmd`) downloads everything else, including the right JavaFX platform jars for your OS.

```bash
git clone https://github.com/zenoxart/GitLens.git
cd GitLens
```

Run it directly:

```bash
./mvnw javafx:run
```

On Windows use `mvnw.cmd javafx:run` instead.

Or build a standalone runnable jar:

```bash
./mvnw -Pdist package
java -jar target/gitlens-*.jar
```

Or package it into the same kind of native app image CI publishes (needs a JDK 21 with `jpackage`, bundled with the JDK itself):

```bash
./mvnw -Pdist package
mkdir jpackage-input && cp target/gitlens-*.jar jpackage-input/
jpackage --type app-image --input jpackage-input --main-jar $(basename target/gitlens-*.jar) \
  --main-class com.codehistorian.Launcher --name GitLens
```

Then in the app: **File → Open Repository** and point it at any local Git repository.

## Configuration

Open **File → Settings...** and paste in your Anthropic API key to enable the AI Assistant panel. It's saved to a per-user settings file and reused on every launch:

| OS | Location |
|---|---|
| Windows | `%APPDATA%\GitLens\settings.json` |
| macOS | `~/Library/Application Support/GitLens/settings.json` |
| Linux | `$XDG_CONFIG_HOME/GitLens/settings.json` (falls back to `~/.config/GitLens/settings.json`) |

The key is stored in plain text, scoped to your user account — don't share this file. Setting the `ANTHROPIC_API_KEY` environment variable also works and is used as a fallback when nothing is saved. Without either, GitLens still retrieves and displays evidence, but answers come back as "AI answers are not configured" instead of an LLM-generated explanation.

GitHub enrichment (pull requests, issues) is implemented in `github/` and `service/GitHubService.java` but doesn't have a settings field of its own yet — see [Features](#features).

## Project structure

```
src/main/java/com/codehistorian/
    Main.java, Launcher.java     entry points (Launcher exists so the shaded jar can run without --module-path)
    ui/          MainController.java — wires the FXML view to the services below
                 SettingsDialog.java — the API key input screen
    service/     RepositoryService, GitHistoryService, GitHubService, SearchService,
                 QuestionService, EvidenceService, CodeAnalysisService, SettingsService
    model/       plain data classes (CommitInfo, PullRequestInfo, IssueInfo, Evidence, AppSettings, ...)
    git/         JGit integration — reading repos, commits, diffs, blame
    github/      GitHub REST client — pull request & issue fetchers
    search/      Apache Lucene index, indexer, search engine
    analysis/    JavaParser-based symbol & dependency extraction
    ai/          LLM client, prompt builder, answer generator
    storage/     JSON cache, the .code-history/ project folder layout, and AppDataLocator
                 (resolves the per-OS settings folder for the saved API key)

src/main/resources/
    fxml/main.fxml     the UI layout
    css/modern.css      dark theme, purple primary color
    icons/              app icon (multiple sizes + .ico + .icns)
```

## Local storage

GitLens keeps a hidden folder inside the repository you open:

```
.code-history/
    repository.json
    github/{pull-requests,issues}/
    index/lucene/
    symbols/symbols.json
    dependencies/graph.json
    cache/
```

It's pure cache. Delete it and GitLens reconstructs everything from Git and GitHub the next time you open the repository.

## Tech stack

Java 21 · JavaFX 21 · JGit · Apache Lucene · Jackson · JavaParser · GitHub REST API · Anthropic API

## License

[MIT](LICENSE)
