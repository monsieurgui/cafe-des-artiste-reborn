# 0 — Goals
- **Rewrite in Java** using only maintained libraries.
- Core = commands / queue. Everything else is a pluggable module.
- Targets: ≤ 50 ms command latency, glitch-free playback, hot-reload config, one-click Docker.

---

# 1 — Tech stack & key deps

| Concern | Library / Tool | Notes |
|---------|----------------|-------|
| Discord gateway | **JDA 5.5.1** | `DefaultShardManager` handles shards/reconnects. |
| Audio decode / Opus | **Lavaplayer 2.2.4** | Pure-Java. |
| YouTube extract | **youtube-source plugin** | Modern YT signature logic. |
| (Optional) external node | **Lavalink 4.1.x** | Off-load audio later. |
| Config | Typesafe Config |
| DI | Dagger 2 |
| Logging | SLF4J + Logback |
| Metrics | Micrometer → Prometheus |
| Tests | JUnit 5 + Mockito |
| Build | Gradle Kotlin DSL |
| Container | Distroless Java 21 |

*(Alt: `yt-dlp-java` wrapper if you must call yt-dlp.)*

---

# 2 — Gradle multi-module layout
cafe-des-artistes/
├─ build.gradle.kts
├─ settings.gradle.kts
├─ bot-app/ ← Main()
├─ bot-core/ ← commands, queue
├─ audio-common/ ← interfaces
├─ audio-lavaplayer/ ← impl
├─ infra-config/
├─ infra-metrics/
└─ test-fixtures/

Public APIs live in **bot-core** so Lavaplayer ↔ Lavalink swap is painless.

---

# 3 — Runtime architecture

JDA Shards ─▶ Command Router ─▶ TrackQueue ─▶ AudioController ─▶ (a) Lavaplayer or (b) Lavalink REST/ws

- Prefetch next track in async thread to avoid gateway blocking.

---

# 4 — Module design

| Module | Key classes | Responsibility |
|--------|-------------|----------------|
| **bot-app** | `Main`, `BotLauncher` | Boot, DI, shard setup. |
| **bot-core** | `SlashCommand`, `TrackQueue`, `AudioController` | Logic, no JDA deps. |
| **audio-common** | `AudioSearchService`, `AudioTrack`, `PlaybackStrategy` | SPI. |
| **audio-lavaplayer** | `LavaplayerSearch`, `LavaplayerPlayback` | Concrete impl. |
| **infra-config** | `ConfigLoader` | HOCON + env merge. |
| **infra-metrics** | `MetricsBinder` | Prometheus export. |

---

# 5 — Playback flow (Lavaplayer)

1. **Search** → `AudioPlayerManager.loadItem`.
2. **Queue** → add to `TrackQueue`.
3. **Prefetch** next track via `CompletableFuture`.
4. **Playback** → `AudioSendHandler` streams 20 ms Opus frames.
5. **Errors** → `AudioEventAdapter`; Resilience4j circuit-break 403/429 loops.

---

# 6 — Config template (HOCON)

```hocon
discord {
  token  = ${?BOT_TOKEN}
  shards = 1
  intents = [ "GUILD_VOICE_STATES", "MESSAGE_CONTENT" ]
}
audio {
  backend = "lavaplayer"   # or "lavalink"
  lavalink.nodes = [{
    host = "lavalink"
    port = 2333
    password = "youshallnotpass"
  }]
}

# 7 — Testing
Unit: queue ordering, command parsing.

Integration: embedded Lavaplayer, assert PCM length.

E2E: fake Discord gateway container.

# 8 — CI / CD
GitHub Actions: ./gradlew test jibDockerBuild.

Push image to GHCR.

Deploy via docker-compose (includes optional Lavalink) or Helm chart.

# 9 — Observability
/metrics → Prometheus / Grafana (latency, drops, CPU).

Logback JSON → Loki.

/health endpoint: shard + Lavalink status.

# 10 — Extensibility hooks
New source: implement AudioSearchService, register with ServiceLoader.

Filters/EQ: Lavaplayer AudioFilter, expose /filter commands.

Persistence: later add bot-data module (Exposed + SQLite).

# Roadmap
Day	Milestone
0	Scaffold modules, JDA login, /ping.
1	Lavaplayer+youtube-source, /play /skip /queue.
2	Prefetch, metrics, Dockerfile.
3	Optional Lavalink, Prometheus, CI/CD.
4+	Filters, lyrics, playlists, persistence.
