# Memory Log

## Dependencies Added
- JDA 5.0.0-beta.20: Discord API library for bot-app and bot-core modules
- LavaPlayer 2.0.4: Audio playback library for bot-player module  
- RabbitMQ amqp-client 5.20.0: Message broker client for inter-service communication
- H2 Database 2.2.224: Embedded database for persistence in bot-app and bot-core
- SLF4J Simple 2.0.9: Logging framework for all modules
- JUnit 4.13.2: Testing framework for all modules

## Project Structure Decisions
- Maven multi-module project with parent POM at root
- Standard Maven directory structure (src/main/java, src/test/java) for all modules
- Docker Compose configuration with separate services for each bot module
- H2 database chosen over PostgreSQL for initial simplicity

## Common Data Models (User Story 2)
- Song POJO: Immutable data class with title, url, thumbnailUrl, duration, requesterId, guildId
- QueueManager interface: Defines contract for queue operations across services
- PlayerStatus enum: PLAYING, PAUSED, STOPPED states for player status communication
- All common models placed in bot-common module for shared access
- Used builder pattern for Song construction to improve readability
- Followed Javadoc documentation standards for all public APIs