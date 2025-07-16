"""Main entrypoint for bot-app service."""

import asyncio
import logging
import os
import re
from typing import Dict, Optional

import asyncpg
import discord
from discord.ext import commands
from dotenv import load_dotenv

from bot_common import Song, PlayerStatus, GuildConfig

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
        
        self.db_pool: Optional[asyncpg.Pool] = None
        self.pending_setups: Dict[int, int] = {}  # user_id -> guild_id

    async def setup_hook(self) -> None:
        """Initialize database connection."""
        db_url = os.getenv("DATABASE_URL", "postgresql://admin:password@localhost:5432/musicbot")
        self.db_pool = await asyncpg.create_pool(db_url)
        
        # Create tables if they don't exist
        async with self.db_pool.acquire() as conn:
            await conn.execute("""
                CREATE TABLE IF NOT EXISTS guild_configs (
                    guild_id BIGINT PRIMARY KEY,
                    channel_id BIGINT NOT NULL,
                    queue_message_id BIGINT,
                    now_playing_message_id BIGINT
                )
            """)

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
            self.pending_setups[owner.id] = guild.id
            logger.info(f"Setup DM sent to {owner.name} for guild {guild.name}")
            
        except discord.Forbidden:
            logger.warning(f"Cannot DM owner {owner.name} for guild {guild.name}")

    async def on_message(self, message: discord.Message) -> None:
        """Handle messages for setup responses (Task 4.4-4.5)."""
        # Ignore bot messages
        if message.author.bot:
            return
            
        # Check if this is a DM from someone with pending setup
        if isinstance(message.channel, discord.DMChannel) and message.author.id in self.pending_setups:
            await self._handle_setup_response(message)
            return
            
        # Process normal commands
        await self.process_commands(message)

    async def _handle_setup_response(self, message: discord.Message) -> None:
        """Handle setup response from guild owner."""
        guild_id = self.pending_setups[message.author.id]
        guild = self.get_guild(guild_id)
        
        if not guild:
            await message.reply("Error: Could not find the server.")
            return
            
        # Extract channel mention from message
        channel_match = re.search(r'<#(\d+)>', message.content)
        if not channel_match:
            await message.reply("Please mention a valid text channel (e.g., #music-bot)")
            return
            
        channel_id = int(channel_match.group(1))
        channel = guild.get_channel(channel_id)
        
        if not channel or not isinstance(channel, discord.TextChannel):
            await message.reply("Please mention a valid text channel that I can access.")
            return
            
        # Store configuration and create embeds (Task 6.1-6.4)
        try:
            async with self.db_pool.acquire() as conn:
                await conn.execute(
                    "INSERT INTO guild_configs (guild_id, channel_id) VALUES ($1, $2) "
                    "ON CONFLICT (guild_id) DO UPDATE SET channel_id = $2",
                    guild_id, channel_id
                )
            
            # Create initial embeds
            queue_embed = discord.Embed(
                title="🎵 Music Queue",
                description="The queue is empty.",
                color=discord.Color.blue()
            )
            
            now_playing_embed = discord.Embed(
                title="🎵 Now Playing",
                description="Nothing is currently playing.",
                color=discord.Color.green()
            )
            
            queue_msg = await channel.send(embed=queue_embed)
            now_playing_msg = await channel.send(embed=now_playing_embed)
            
            # Update database with message IDs
            async with self.db_pool.acquire() as conn:
                await conn.execute(
                    "UPDATE guild_configs SET queue_message_id = $1, now_playing_message_id = $2 WHERE guild_id = $3",
                    queue_msg.id, now_playing_msg.id, guild_id
                )
            
            # Remove from pending setups
            del self.pending_setups[message.author.id]
            
            await message.reply(f"✅ Setup complete! Music bot is now configured for {channel.mention}")
            logger.info(f"Setup completed for guild {guild.name} with channel {channel.name}")
            
        except Exception as e:
            logger.error(f"Error during setup: {e}")
            await message.reply("❌ An error occurred during setup. Please try again.")

    async def _check_unconfigured_guilds(self) -> None:
        """Check all guilds for configuration on startup (Task 5.1-5.3)."""
        if not self.db_pool:
            return
            
        for guild in self.guilds:
            async with self.db_pool.acquire() as conn:
                result = await conn.fetchrow(
                    "SELECT channel_id FROM guild_configs WHERE guild_id = $1",
                    guild.id
                )
                
            if not result:
                logger.info(f"Guild {guild.name} not configured, triggering setup")
                await self._setup_guild(guild)
            else:
                logger.info(f"Guild {guild.name} already configured")


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