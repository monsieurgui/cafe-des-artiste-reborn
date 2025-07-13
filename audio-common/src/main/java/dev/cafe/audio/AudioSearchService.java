package dev.cafe.audio;

import java.util.concurrent.CompletableFuture;

/** Service for searching and loading audio tracks. */
public interface AudioSearchService {
  CompletableFuture<SearchResult> search(String query);

  CompletableFuture<SearchResult> loadTrack(String url);
}
