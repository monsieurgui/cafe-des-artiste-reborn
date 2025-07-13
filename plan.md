# plan.md  —  “Channel-link & Persistent Posts” feature  
> **Scope:** implement whisper-based setup flow, guild channel linking, two persistent status posts, and ephemeral command replies.  
> After a task is completed, move it to **Done** with the commit hash.

---

## Todo

| # | Task (Conventional-Commit style) | Acceptance criteria |
|---|----------------------------------|---------------------|
| 2 | **feat(setup): introduce `/setup` slash command (guild-only)** | Requires `Administrator` permission; responds with ephemeral “Check your DMs”. |
| 3 | **feat(dm): implement whisper flow** | Bot DMs the invoking admin:<br>  a. asks “Link a text channel for queue/now-playing?”<br>  b. waits for channel mention reply (timeout 2 min).<br>  c. validates bot has `SEND_MESSAGES` & `MANAGE_MESSAGES` in that channel.<br>  d. stores `channelId` in `guild_settings`. |
| 4 | **feat(posts): create two pinned messages in linked channel** | a. Top message content: “🎶 **Queue** (auto-updated)”<br>b. Second message: “▶️ **Now playing…**” placeholder.<br>Both IDs stored in DB; messages pinned. |
| 5 | **feat(queue-display): update queue message on TrackQueue changes** | Edits top post embed whenever queue mutates; integration test uses mock JDA. |
| 6 | **feat(now-playing): update now-playing message every 15 s** | Shows title, requester, elapsed/total time progress bar (ASCII ░/█). Stops updates when paused or track ends. |
| 7 | **refactor(cmd): make all command replies ephemeral & auto-delete** | Slash command handlers respond with `setEphemeral(true)`; schedule delete after 60 s via `CompletableFuture.delayedExecutor`. |
| 8 | **test(flow): e2e test of setup→link→play** | Using JDA mock gateway: run `/setup`, link a fake channel, `/play` a track → verify DB rows + edited posts. |
| 9 | **docs(setup): update README** | Explain `/setup` flow, required permissions, and auto-updated posts behaviour. |

---

## Done
*(move completed tasks here)*

| # | Commit | Description |
|---|--------|-------------|
| 1 | d46c18d | **chore(data): add GuildSettings table (SQLite)** |
