# CLAUDE.md — Project Constitution  
_All rule numbers are stable; cite them when you comment or create tasks._

---

## 1 — Mission  
Build and maintain a **Java-based modular Discord music bot** (“Cafe-des-Artistes”) whose core handles commands and queue logic while all other concerns are plug-ins. Target ≤ 50 ms command latency, glitch-free audio, hot-reloadable config, and one-click Docker deployment.

## 2 — Scope & Non-scope  
- **In scope:** Java source in `/src`, Gradle and Docker assets, `plan.md`, `memory.md`.  
- **Out of scope:** Documentation sites, marketing material, Traefik, unrelated infra.

## 3 — Coding standards  
- Follow **Google Java Style** (2-space indents, 120-char wrap).  
- No wildcard imports.  
- Public classes require a one-line Javadoc summary.  
- Use Java 21 language features; no preview APIs.  

## 4 — Dependency policy  
- Allowed libraries are listed in `memory.md`.  
- Additions require a new bullet in `memory.md` with date and reason.  
- No direct JNA/native deps; favor pure-Java first.

## 5 — Project structure (immutable)  
/bot-app // Main + DI
/bot-core // Commands, queue, no JDA imports
/audio-common // Interfaces
/audio-<impl> // e.g. audio-lavaplayer
/infra-config
/infra-metrics

Do **not** move or rename these modules without an approved task.

## 6 — Commit format (Conventional Commits)  
`<type>(<scope>): <subject>`  
Allowed `<type>`: **feat**, **fix**, **refactor**, **test**, **docs**, **chore**.  
Body (optional) line-wrapped at 72 chars. Example:  
feat(audio): add /pause command


## 7 — IO Contract  
When executing a task from `plan.md`, reply **only** with:  
1. A unified git diff in a ```patch fenced block.  
2. The commit message line after the diff.  
_No extra prose, logs, or explanations._

## 8 — plan.md rules  
- Top unchecked task = next action.  
- After completing a task, move it to **Done** with the commit hash.  
- Do not append more than five unchecked tasks at a time.

## 9 — memory.md rules  
- One bullet per line, max 120 chars.  
- Append new immutable facts; never edit or delete existing bullets unless a human task says so.  
- Only update `memory.md` in a commit whose subject starts with `chore(memory):`.

## 10 — Safety rails  
- **SR-10.1** Never regenerate `gradle-wrapper.jar`, `gradlew`, or lockfiles unless task says so.  
- **SR-10.2** `traefik.enable` labels must remain `false` in every compose file.  
- **SR-10.3** Never touch CI/CD workflows.  
- **SR-10.4** Do not modify or delete this CLAUDE.md.

## 11 — Testing & CI  
- All code must compile with `./gradlew build`.  
- Run `./gradlew test`; coverage must stay ≥ 80 %.  
- Lint with `./gradlew spotlessCheck`.  
- If tests fail, abort the patch and request clarification.

## 12 — Uncertainty / Clarification  
If you encounter missing information or an impossible constraint:  
1. Commit nothing.  
2. Respond with `BLOCKED:` followed by a concise question.  

_End of constitution_
