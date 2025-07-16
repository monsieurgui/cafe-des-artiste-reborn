"""Database models and utilities for bot-common."""

from dataclasses import dataclass
from typing import Optional


@dataclass
class GuildConfig:
    """Guild configuration stored in database."""
    guild_id: int
    channel_id: int
    queue_message_id: Optional[int] = None
    now_playing_message_id: Optional[int] = None