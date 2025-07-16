from dataclasses import dataclass
from enum import Enum
from typing import Optional


@dataclass
class Song:
    title: str
    url: str
    thumbnail_url: Optional[str]
    duration: int  # Duration in seconds
    requester_id: int
    guild_id: int


class PlayerStatus(Enum):
    PLAYING = "playing"
    PAUSED = "paused"
    STOPPED = "stopped"