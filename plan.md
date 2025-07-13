# plan.md
> Only the **Todo** list is read/executed by Claude.  
> After completing a task, move it to **Done** with the commit hash.



### todo

| # | Task | Acceptance criteria |
|---|------|---------------------|
| C4 | **feat(cache): `/cache stats` & `/cache clear` commands** | Stats embed shows top 10 cached; clear deletes files & DB flags. |
| C5 | **chore(ci): nightly GitHub Action to prune cache (>60 days OR >10 GB)** | Job logs deleted MBs; preserves metrics. |

---

## Done
*(move completed tasks here)*

| # | Commit | Description |
|---|--------|-------------|
| C1 | bb6e66c | **feat(cache): create MostPlayedService (SQLite)** |
| C2 | 765518e | **feat(cache): download track to `cache/` when playCount ≥ 5** |
| C3 | 77fde4d | **feat(cache): FileCachePlaybackStrategy** |

