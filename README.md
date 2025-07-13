# Cafe des Artistes 🎵

A high-performance, modular Discord music bot built with Java 21. Features dual audio backends (Lavaplayer/Lavalink), playlist management, and enterprise-grade monitoring.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java 21](https://img.shields.io/badge/java-21-orange.svg)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)]()

## ✨ Features

- 🎶 **Multi-Source Audio**: YouTube, SoundCloud, Bandcamp, Vimeo, Twitch streams
- 🔧 **Dual Audio Backends**: Choose between Lavaplayer (embedded) or Lavalink (server)
- 📋 **Playlist Management**: Create, save, and share custom playlists
- ⚡ **High Performance**: ≤50ms command latency with concurrent guild support
- 📊 **Built-in Monitoring**: Prometheus metrics and health endpoints
- 🐳 **Docker Ready**: One-command deployment with Docker Compose
- 🔄 **Hot-Reloadable**: Configuration updates without restarts
- 🏗️ **Modular Architecture**: Clean separation of concerns

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Docker & Docker Compose (for easy deployment)
- Discord bot token ([Create one here](https://discord.com/developers/applications))

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/cafe-des-artistes.git
   cd cafe-des-artistes
   ```

2. **Set environment variables**
   ```bash
   export BOT_TOKEN="your_discord_bot_token_here"
   ```

3. **Start the bot**
   ```bash
   docker-compose up -d
   ```

The bot will start with Lavalink backend by default. Check logs with:
```bash
docker-compose logs -f bot
```

### Option 2: Local Development

1. **Build the project**
   ```bash
   ./gradlew build
   ```

2. **Set required environment variables**
   ```bash
   export BOT_TOKEN="your_discord_bot_token_here"
   ```

3. **Run the bot**
   ```bash
   ./gradlew :bot-app:run
   ```

## ⚙️ Configuration

### Environment Variables

#### Required

| Variable | Description | Example |
|----------|-------------|---------|
| `BOT_TOKEN` | Discord bot token | `MTIzNDU2Nzg5MGFiY2RlZi...` |

#### Optional

| Variable | Description | Default | Options |
|----------|-------------|---------|---------|
| `AUDIO_BACKEND` | Audio backend to use | `lavaplayer` | `lavaplayer`, `lavalink` |
| `LAVALINK_HOST` | Lavalink server hostname | `lavalink` | Any hostname |
| `LAVALINK_PORT` | Lavalink server port | `2333` | Any valid port |
| `LAVALINK_PASSWORD` | Lavalink server password | `youshallnotpass` | Any string |

### Backend Comparison

| Feature | Lavaplayer | Lavalink |
|---------|------------|----------|
| **Setup** | ✅ Zero config | ⚙️ External server |
| **Performance** | 🔥 Lower latency | 🔥 Better for large bots |
| **Resources** | 💾 More memory | 💾 Distributed load |
| **Scaling** | 👥 Single instance | 👥 Multi-instance |
| **YouTube** | ✅ Direct support | ✅ Plugin support |

### Development Configuration

For development, you can override settings in `bot-app/src/main/resources/application.conf`:

```hocon
discord {
  token = ${?BOT_TOKEN}
  shards = 1
}

audio {
  backend = ${?AUDIO_BACKEND:"lavaplayer"}
  lavalink {
    host = ${?LAVALINK_HOST:"localhost"}
    port = ${?LAVALINK_PORT:2333}
    password = ${?LAVALINK_PASSWORD:"youshallnotpass"}
  }
}
```

## 🎵 Commands

### Music Controls

| Command | Description | Parameters |
|---------|-------------|------------|
| `/play <query>` | Play a song or add to queue | `query`: Song name or URL<br>`search`: Show search results (optional) |
| `/search <query>` | Search for tracks | `query`: Song name |
| `/skip` | Skip current song | None |
| `/stop` | Stop and clear queue | None |
| `/pause` | Pause playback | None |
| `/resume` | Resume playback | None |
| `/queue` | Show current queue | None |

### Voice Controls

| Command | Description |
|---------|-------------|
| `/join` | Join your voice channel |
| `/leave` | Leave voice channel |

### Playlists

| Command | Description | Parameters |
|---------|-------------|------------|
| `/playlist create <name>` | Create new playlist | `name`: Playlist name |
| `/playlist list` | Show your playlists | None |
| `/playlist load <id>` | Load playlist to queue | `id`: Playlist ID |
| `/playlist show <id>` | Display playlist contents | `id`: Playlist ID |

### Utility

| Command | Description |
|---------|-------------|
| `/ping` | Check bot responsiveness |

## 🎯 Usage Examples

### Basic Playback
```
/join                    # Bot joins your voice channel
/play Rick Astley        # Searches and plays "Rick Astley"
/play search:true Beethoven  # Shows search results to choose from
/play https://youtube.com/watch?v=... # Direct URL playback
/queue                   # View current queue
/skip                    # Skip to next song
```

### Playlist Management
```
/playlist create My Favorites     # Create playlist
/search My Chemical Romance       # Search for tracks
[Click ➕ button on results]      # Add to playlist
/playlist list                    # View your playlists
/playlist load 550e8400-e29b-...  # Load playlist to queue
```

### Supported Sources

- 🎥 **YouTube** - Videos and playlists
- 🎵 **SoundCloud** - Tracks and sets
- 🎸 **Bandcamp** - Albums and tracks
- 📺 **Vimeo** - Videos
- 🎮 **Twitch** - Live streams and VODs
- 🌐 **HTTP URLs** - Direct audio files
- 📁 **Local Files** - Server-side files (when applicable)

## 📊 Monitoring

The bot exposes Prometheus metrics and health endpoints:

- **Metrics**: `http://localhost:8080/metrics`
- **Health**: `http://localhost:8080/health`

### Key Metrics

- `bot_commands_executed_total` - Command usage statistics
- `bot_commands_latency` - Command response times
- `bot_tracks_played_total` - Track playback count
- `bot_guilds_active` - Connected guilds
- `bot_tracks_queued` - Current queue sizes

### Health Check Response

```json
{
  "bot": {
    "status": "CONNECTED",
    "ping": 45,
    "guilds": 12,
    "shards": {"current": 0, "total": 1}
  },
  "audio": {
    "backend": "lavaplayer"
  },
  "timestamp": 1704067200000,
  "status": "UP"
}
```

## 🏗️ Development

### Project Structure

```
cafe-des-artistes/
├── bot-app/           # Main application & DI
├── bot-core/          # Commands & queue logic
├── audio-common/      # Audio interfaces
├── audio-lavaplayer/  # Lavaplayer implementation
├── audio-lavalink/    # Lavalink implementation
├── infra-config/      # Configuration management
└── infra-metrics/     # Monitoring & metrics
```

### Building

```bash
# Full build with tests and checks
./gradlew build

# Run specific module
./gradlew :bot-app:run

# Code formatting
./gradlew spotlessApply

# Generate Docker image
./gradlew :bot-app:jibDockerBuild
```

### Code Style

- **Format**: Google Java Style (enforced by Spotless)
- **Language**: Java 21 features enabled
- **Architecture**: Dependency injection with Dagger
- **Testing**: JUnit 5 + Mockito

### Adding Audio Sources

1. Implement `AudioSearchService` and `PlaybackStrategy`
2. Add module to `settings.gradle.kts`
3. Register in `BotModule.java`
4. Update configuration as needed

## 🐳 Docker Deployment

### Production Deployment

1. **Create production environment file**
   ```bash
   cat > .env << EOF
   BOT_TOKEN=your_production_token_here
   AUDIO_BACKEND=lavalink
   LAVALINK_PASSWORD=your_secure_password_here
   EOF
   ```

2. **Deploy with custom configuration**
   ```bash
   docker-compose --env-file .env up -d
   ```

3. **Monitor deployment**
   ```bash
   docker-compose ps
   docker-compose logs -f
   ```

### Custom Docker Images

Build your own image:
```bash
./gradlew :bot-app:jibDockerBuild
docker tag cafe-des-artistes:latest your-registry/cafe-des-artistes:v1.0.0
docker push your-registry/cafe-des-artistes:v1.0.0
```

## 🔧 Troubleshooting

### Common Issues

#### Bot doesn't respond to commands
- ✅ Check `BOT_TOKEN` is set correctly
- ✅ Verify bot has proper Discord permissions
- ✅ Ensure bot is in the same server as you
- ✅ Check Docker logs: `docker-compose logs bot`

#### Audio doesn't play
- ✅ Bot must be in a voice channel: `/join` first
- ✅ Check audio backend status in `/health` endpoint
- ✅ For Lavalink: verify Lavalink server is running
- ✅ For YouTube: ensure plugin is working

#### Performance issues
- ✅ Monitor metrics at `http://localhost:8080/metrics`
- ✅ Check command latency in health endpoint
- ✅ Consider switching to Lavalink for better scaling
- ✅ Verify Java heap settings in Docker

#### Configuration not loading
- ✅ Environment variables take precedence over config files
- ✅ Use `docker-compose config` to verify environment
- ✅ Check `application.conf` syntax (HOCON format)

### Getting Help

1. **Check logs first**: `docker-compose logs -f`
2. **Verify configuration**: `http://localhost:8080/health`
3. **Review metrics**: `http://localhost:8080/metrics`
4. **Create issue**: Include logs and configuration (without tokens!)

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Development Guidelines

- Follow Google Java Style (automatic with `./gradlew spotlessApply`)
- Add tests for new features
- Update documentation for API changes
- Ensure all builds pass: `./gradlew build`

## 🙏 Acknowledgments

- [JDA](https://github.com/discord-jda/JDA) - Java Discord API
- [Lavaplayer](https://github.com/lavalink-devs/lavaplayer) - Audio source manager
- [Lavalink](https://github.com/lavalink-devs/Lavalink) - Distributed audio server
- [TypeSafe Config](https://github.com/lightbend/config) - Configuration library

---

Made with ❤️ for the Discord music community