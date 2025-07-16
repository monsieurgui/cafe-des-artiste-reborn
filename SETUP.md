# Discord Music Bot - Setup Guide

## Environment Configuration

### 1. Setup Environment Variables

Copy the example environment file and configure it:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```bash
# Required: Get your Discord bot token from https://discord.com/developers/applications
DISCORD_BOT_TOKEN=your_actual_discord_bot_token_here

# Optional: Adjust other settings as needed
DATABASE_URL=jdbc:h2:tcp://database:9092/./data/bot
RABBITMQ_URL=amqp://guest:guest@rabbitmq:5672/
JAVA_OPTS=-Xms512m -Xmx1024m
LOG_LEVEL=INFO
```

### 2. Create Discord Bot

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to "Bot" section and create a bot
4. Copy the bot token and paste it into your `.env` file
5. Enable required bot permissions:
   - Send Messages
   - Embed Links
   - Connect (Voice)
   - Speak (Voice)
   - Use Voice Activity

### 3. Run with Docker

Start all services:

```bash
docker-compose up -d
```

Check logs:

```bash
docker-compose logs -f bot-app
```

Stop services:

```bash
docker-compose down
```

### 4. Access Services

- **RabbitMQ Management**: http://localhost:15672 
  - Username: `guest`
  - Password: `guest`
- **H2 Database Console**: http://localhost:8082
  - JDBC URL: `jdbc:h2:tcp://localhost:9092/./data/bot`
  - User: `sa`
  - Password: (empty)

### 5. Verify Setup

Check that all services are running:

```bash
docker-compose ps
```

View logs for a specific service:

```bash
docker-compose logs -f bot-app
```

### 6. Invite Bot to Server

1. Go to OAuth2 → URL Generator in Discord Developer Portal
2. Select scopes: `bot`, `applications.commands`
3. Select permissions: Send Messages, Embed Links, Connect, Speak
4. Use generated URL to invite bot to your server

## Development

### Environment Variables Reference

| Variable | Description | Default |
|----------|-------------|---------|
| `DISCORD_BOT_TOKEN` | Discord bot token (required) | - |
| `DATABASE_URL` | H2 database connection string | `jdbc:h2:tcp://database:9092/./data/bot` |
| `DATABASE_USER` | Database username | `sa` |
| `DATABASE_PASSWORD` | Database password | (empty) |
| `RABBITMQ_URL` | RabbitMQ connection string | `amqp://guest:guest@rabbitmq:5672/` |
| `RABBITMQ_USER` | RabbitMQ username | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `guest` |
| `JAVA_OPTS` | JVM options | `-Xms512m -Xmx1024m` |
| `LOG_LEVEL` | Logging level | `INFO` |

### Security Notes

- Never commit the `.env` file to version control
- The `.env` file is already in `.gitignore`
- Use `.env.example` as a template for other developers
- Rotate your Discord bot token if accidentally exposed