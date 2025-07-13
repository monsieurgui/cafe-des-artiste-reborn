# plan.md
> Only the **Todo** list is read/executed by Claude.  
> After completing a task, move it to **Done** with the commit hash.



### todo

| # | Task | Acceptance criteria |
|---|------|---------------------|
| C2 | **feat(cache): download track to `cache/` when playCount ≥ 5** | File saved as `<videoId>.opus`; entry in DB flagged `cached=true`. |
| C3 | **feat(cache): FileCachePlaybackStrategy** | If cached file exists → play local; else stream; on missing/corrupt file auto-streams and flags stale. |
| C4 | **feat(cache): `/cache stats` & `/cache clear` commands** | Stats embed shows top 10 cached; clear deletes files & DB flags. |
| C5 | **chore(ci): nightly GitHub Action to prune cache (>60 days OR >10 GB)** | Job logs deleted MBs; preserves metrics. |

---

## Done
*(move completed tasks here)*

| # | Commit | Description |
|---|--------|-------------|
| C1 | bb6e66c | **feat(cache): create MostPlayedService (SQLite)** |

