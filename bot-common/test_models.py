"""Unit tests for bot-common models."""

import pytest
from models import Song, PlayerStatus


def test_song_creation():
    """Test Song dataclass creation with all required fields."""
    song = Song(
        title="Test Song",
        url="https://example.com/song",
        thumbnail_url="https://example.com/thumb.jpg",
        duration=180,
        requester_id=12345,
        guild_id=67890
    )
    
    assert song.title == "Test Song"
    assert song.url == "https://example.com/song"
    assert song.thumbnail_url == "https://example.com/thumb.jpg"
    assert song.duration == 180
    assert song.requester_id == 12345
    assert song.guild_id == 67890


def test_song_optional_thumbnail():
    """Test Song creation with None thumbnail_url."""
    song = Song(
        title="No Thumb Song",
        url="https://example.com/song2",
        thumbnail_url=None,
        duration=240,
        requester_id=11111,
        guild_id=22222
    )
    
    assert song.thumbnail_url is None


def test_player_status_enum():
    """Test PlayerStatus enum values."""
    assert PlayerStatus.PLAYING.value == "playing"
    assert PlayerStatus.PAUSED.value == "paused"
    assert PlayerStatus.STOPPED.value == "stopped"