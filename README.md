# Cafe Des Artistes Music Bot

A Python-based modular Discord music bot built with a microservices architecture. The bot provides glitch-free audio playback with high responsiveness through containerized services that communicate via RabbitMQ message queues.

## Project Structure

This project is organized into the following modules:

### `/bot-app/`
Main application entrypoint, dependency injection, and core bot event listeners. This module initializes the bot and handles Discord gateway events.

### `/bot-core/`
Handles Discord commands and UI-facing logic including embeds, slash commands, and user interactions. This is the primary interface between users and the bot's functionality.

### `/bot-player/`
Manages audio playback, queue logic, and yt-dlp interaction. This service is responsible for the actual music streaming and playback control.

### `/bot-common/`
Contains shared data structures, interfaces, and DTOs used across all microservices for consistent communication.

### `/infra-config/`
Contains Dockerfiles, docker-compose.yml, and other infrastructure configuration files needed for deployment.

## Architecture

The bot follows a microservices architecture where:
- All inter-service communication happens asynchronously via RabbitMQ
- Services are containerized using Docker
- One-command deployment via Docker Compose
- Clear separation of concerns between modules

## Getting Started

*Setup instructions will be added as development progresses.*

## Development

This project follows strict coding standards including PEP 8 compliance, black formatting, and comprehensive type hints. See `CLAUDE.md` for detailed development guidelines.