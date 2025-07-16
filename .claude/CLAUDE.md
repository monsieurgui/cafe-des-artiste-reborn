# CLAUDE.md — Project Constitution
_All rule numbers are stable; cite them when you comment or create tasks._

This document defines the rules of engagement for building the Python Discord Music Bot. Adherence to these rules is mandatory for all contributions.

---

## 1 — Mission
Build and maintain a **Python-based modular Discord music bot**. The bot's architecture will be composed of containerized microservices that communicate via a message queue (RabbitMQ). Key objectives include glitch-free audio playback, high responsiveness, a clear separation of concerns between services, and simple, one-command deployment via Docker Compose.

## 2 — Scope & Non-scope
*   **In scope:** All Python source code within the module directories, Docker assets (`Dockerfile`, `docker-compose.yml`), dependency files (`requirements.txt`), and the `plan.md` and `memory.md` files.
*   **Out of scope:** CI/CD pipeline configurations (e.g., GitHub Actions workflows), external infrastructure management, and documentation intended for end-users.

## 3 — Coding Standards
*   **Style:** All Python code must strictly adhere to the **PEP 8** style guide.
*   **Formatting:** All code must be formatted using **`black`**.
*   **Linting:** All code must pass linting with **`flake8`** with no errors.
*   **Imports:** No wildcard imports (e.g., `from module import *`). Imports must be absolute and sorted.
*   **Docstrings:** All public modules, functions, classes, and methods must have a concise, one-line summary docstring.
*   **Typing:** All function signatures and variable declarations must use **type hints**.

## 4 — Dependency Policy
*   **Management:** Dependencies are managed in a `requirements.txt` file within each service's directory.
*   **Approval:** Allowed libraries are listed in `memory.md`. To add a new dependency, a task must be created in `plan.md`, and upon completion, the library must be added to `memory.md` with the date and reason.
*   **Preference:** Favor pure-Python packages. Dependencies that require compiled C extensions need explicit approval via a task in `plan.md`.

## 5 — Project Structure (Immutable)
The project is divided into the following modules. Do **not** move, rename, or add modules without an approved task.
/bot-app/ # Main application entrypoint, DI, and core bot event listeners
/bot-core/ # Handles Discord commands and UI-facing logic (embeds)
/bot-player/ # Handles audio playback, queue logic, and yt-dlp interaction
/bot-common/ # Contains shared data structures (e.g., interfaces, DTOs)
/infra-config/ # Contains Dockerfiles, docker-compose.yml, and other config


## 6 — Inter-Service Communication
*   All communication between services (e.g., `bot-core` to `bot-player`) **must** be asynchronous via **RabbitMQ**.
*   There will be no direct synchronous calls (e.g., HTTP requests) between the Python services.
*   Shared message formats and contracts will be defined in `bot-common/`.

## 7 — Commit Format (Conventional Commits)
All commit messages must follow the Conventional Commits specification:
`<type>(<scope>): <subject>`

*   **Allowed `<type>`:** **feat**, **fix**, **refactor**, **test**, **docs**, **chore**.
*   **`<scope>`:** The name of the module being changed (e.g., `player`, `core`, `app`).
*   **Bug Fixes:** When the `<type>` is `fix`, you must first analyze the provided `logs.txt` file (if it exists) to diagnose the root cause of the error before proposing a patch.
*   **Example:** `feat(player): implement song skipping logic`

## 8 — IO Contract (Strict)
When executing a task from `plan.md` that involves code changes, your reply must contain **only** the following, in this exact order:
1.  A single, unified git diff in a `patch` fenced code block.
2.  The full, correctly formatted commit message on the line immediately following the diff block.

_No extra prose, greetings, logs, or explanations are permitted in the response._

## 9 — `plan.md` Rules
*   The top unchecked task in `plan.md` is always the next action to be performed.
*   Upon completing a task, move its text to a **`## Done`** section at the bottom of the file, followed by the commit hash.
*   Do not add more than five new, unchecked tasks at a time. Tasks must be granular and follow the single-responsibility principle.

## 10 — `memory.md` Rules
*   This file is an append-only log of immutable facts, decisions, and dependency additions.
*   One bullet point per line, maximum 120 characters.
*   Never edit or delete existing lines unless explicitly instructed by a human.
*   Only update `memory.md` in a commit whose subject is `chore(memory): <reason for update>`.

## 11 — Testing & CI
*   All new features or fixes must be accompanied by unit tests.
*   All tests must pass when run with `pytest`.
*   If a change causes tests to fail, abort the patch and request clarification.

## 12 — Safety Rails
*   **SR-12.1:** Never modify this `CLAUDE.md` file.
*   **SR-12.2:** Do not modify CI/CD workflow files (e.g., in `.github/workflows/`).
*   **SR-12.3:** Do not expose service ports in `docker-compose.yml` to the host machine (e.g., `ports: "8000:8000"`) unless a task explicitly requires it for debugging.
*   **SR-12.4:** Never commit secrets, tokens, or API keys directly into the source code. Use environment variables defined in `docker-compose.yml`.

## 13 — Uncertainty & Clarification
If you encounter an impossible constraint, missing information, or ambiguity that prevents you from completing a task as specified:
1.  Commit nothing.
2.  Respond with the single word `BLOCKED:` followed by a concise, specific question.

---
_End of constitution_