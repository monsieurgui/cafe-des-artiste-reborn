package dev.cafe.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.cafe.audio.AudioSearchService;
import dev.cafe.audio.SearchResult;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Lavaplayer implementation of AudioSearchService.
 */
@Singleton
public class LavaplayerSearchService implements AudioSearchService {
  private final AudioPlayerManager playerManager;

  @Inject
  public LavaplayerSearchService() {
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
  }

  @Override
  public CompletableFuture<SearchResult> search(String query) {
    String searchQuery = query.startsWith("http") ? query : "ytsearch:" + query;
    return loadItem(searchQuery);
  }

  @Override
  public CompletableFuture<SearchResult> loadTrack(String url) {
    return loadItem(url);
  }

  private CompletableFuture<SearchResult> loadItem(String identifier) {
    CompletableFuture<SearchResult> future = new CompletableFuture<>();
    
    playerManager.loadItem(identifier, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        future.complete(LavaplayerSearchResult.trackLoaded(track));
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
          future.complete(LavaplayerSearchResult.searchResult(playlist.getTracks()));
        } else {
          future.complete(LavaplayerSearchResult.playlistLoaded(playlist));
        }
      }

      @Override
      public void noMatches() {
        future.complete(LavaplayerSearchResult.noMatches());
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        future.complete(LavaplayerSearchResult.loadFailed(exception));
      }
    });
    
    return future;
  }

  public AudioPlayerManager getPlayerManager() {
    return playerManager;
  }
}