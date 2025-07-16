from abc import ABC, abstractmethod
from typing import List, Optional
from .models import Song


class Queue(ABC):
    @abstractmethod
    def add_song(self, song: Song) -> None:
        """Add a song to the queue."""
        pass

    @abstractmethod
    def get_next_song(self) -> Optional[Song]:
        """Get and remove the next song from the queue."""
        pass

    @abstractmethod
    def remove_song(self, song_index: int) -> bool:
        """Remove a song at the specified index. Returns True if successful."""
        pass

    @abstractmethod
    def get_queue(self) -> List[Song]:
        """Get the current queue as a list."""
        pass

    @abstractmethod
    def clear_queue(self) -> None:
        """Clear all songs from the queue."""
        pass