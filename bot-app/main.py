"""Main entrypoint for bot-app service."""

import asyncio
import logging
import os
from typing import Optional

import discord
from discord.ext import commands
from dotenv import load_dotenv

from bot_common import Song, PlayerStatus

load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class MusicBot(commands.Bot):
    """Discord music bot main class."""

    def __init__(self) -> None:
        """Initialize the bot with required intents."""
        intents = discord.Intents.default()
        intents.message_content = True
        intents.guilds = True
        intents.voice_states = True

        super().__init__(
            command_prefix="!",
            intents=intents,
            description="A modular Discord music bot"
        )

    async def on_ready(self) -> None:
        """Called when the bot is ready."""
        logger.info(f"Bot is ready! Logged in as {self.user}")
        
        # Check for unconfigured guilds on startup (Task 5.1-5.3)
        await self._check_unconfigured_guilds()

    async def on_guild_join(self, guild: discord.Guild) -> None:
        """Handle when bot joins a new guild (Task 4.1-4.5)."""
        logger.info(f"Joined guild: {guild.name} (ID: {guild.id})")
        await self._setup_guild(guild)

    async def _setup_guild(self, guild: discord.Guild) -> None:
        """Send setup DM to guild owner."""
        owner = guild.owner
        if not owner:
            logger.warning(f"No owner found for guild {guild.name}")
            return

        try:
            embed = discord.Embed(
                title="Welcome to Music Bot!",
                description=(
                    "Thanks for adding me to your server! To get started, "
                    "please mention a text channel where I should post my "
                    "music queue and now playing messages.\n\n"
                    "Example: #music-bot"
                ),
                color=discord.Color.blue()
            )
            
            await owner.send(embed=embed)
            logger.info(f"Setup DM sent to {owner.name} for guild {guild.name}")
            
        except discord.Forbidden:
            logger.warning(f"Cannot DM owner {owner.name} for guild {guild.name}")

    async def _check_unconfigured_guilds(self) -> None:
        """Check all guilds for configuration on startup."""
        for guild in self.guilds:
            # TODO: Check database for guild configuration
            # For now, assume all guilds need setup
            logger.info(f"Checking configuration for guild: {guild.name}")
            # TODO: If not configured, call _setup_guild(guild)


async def main() -> None:
    """Main entry point."""
    token = os.getenv("DISCORD_BOT_TOKEN")
    if not token:
        logger.error("DISCORD_BOT_TOKEN environment variable is required")
        return

    bot = MusicBot()
    
    try:
        await bot.start(token)
    except KeyboardInterrupt:
        logger.info("Bot shutdown requested")
    finally:
        await bot.close()


if __name__ == "__main__":
    asyncio.run(main())