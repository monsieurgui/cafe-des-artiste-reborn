"""Main entrypoint for bot-core service."""

import asyncio
import logging
import os
from typing import Optional

import discord
from discord.ext import commands
from dotenv import load_dotenv

from bot_common import Song, PlayerStatus, GuildConfig

load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class CoreBot(commands.Bot):
    """Bot-core service for handling Discord commands."""

    def __init__(self) -> None:
        """Initialize the bot with required intents."""
        intents = discord.Intents.default()
        intents.message_content = True
        intents.guilds = True
        intents.voice_states = True

        super().__init__(
            command_prefix="!",
            intents=intents,
            description="Music bot core service"
        )

    async def on_ready(self) -> None:
        """Called when the bot is ready."""
        logger.info(f"Bot-core is ready! Logged in as {self.user}")
        
        # Sync slash commands
        try:
            synced = await self.tree.sync()
            logger.info(f"Synced {len(synced)} slash commands")
        except Exception as e:
            logger.error(f"Failed to sync commands: {e}")

    @discord.app_commands.command(name="play", description="Add a song to the queue")
    @discord.app_commands.describe(query="Search query or URL for the song")
    async def play(self, interaction: discord.Interaction, query: str) -> None:
        """Play command handler (Task 7.1-7.4)."""
        # Check if user is in a voice channel (Task 7.4)
        if not interaction.user.voice or not interaction.user.voice.channel:
            await interaction.response.send_message(
                "❌ You need to be in a voice channel to use this command!",
                ephemeral=True
            )
            return
            
        # Acknowledge the command immediately (Task 7.3)
        await interaction.response.defer(ephemeral=True)
        
        try:
            # TODO: Implement yt-dlp search logic (Task 7.5-7.7)
            # TODO: Send to RabbitMQ queue (Task 8.1-8.2)
            
            await interaction.followup.send(
                f"🎵 Added to queue: `{query}`\n"
                f"*This is a placeholder - full implementation coming soon*",
                ephemeral=True
            )
            
        except Exception as e:
            logger.error(f"Error in play command: {e}")
            await interaction.followup.send(
                "❌ An error occurred while processing your request.",
                ephemeral=True
            )


async def main() -> None:
    """Main entry point."""
    token = os.getenv("DISCORD_BOT_TOKEN")
    if not token:
        logger.error("DISCORD_BOT_TOKEN environment variable is required")
        return

    bot = CoreBot()
    
    try:
        await bot.start(token)
    except KeyboardInterrupt:
        logger.info("Bot-core shutdown requested")
    finally:
        await bot.close()


if __name__ == "__main__":
    asyncio.run(main())