"""Unit tests for bot-common interfaces."""

import pytest
from abc import ABC
from typing import List, Optional
from interfaces import Queue
from models import Song


class TestQueue:
    """Test Queue interface implementation."""
    
    def test_queue_is_abstract(self):
        """Test that Queue is an abstract base class."""
        assert issubclass(Queue, ABC)
        
        with pytest.raises(TypeError):
            Queue()
    
    def test_queue_methods_exist(self):
        """Test that Queue has all required abstract methods."""
        methods = ['add_song', 'get_next_song', 'remove_song', 'get_queue', 'clear_queue']
        
        for method in methods:
            assert hasattr(Queue, method)
            assert callable(getattr(Queue, method))