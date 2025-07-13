package dev.cafe.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.core.AudioController;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Lavaplayer implementation of PlaybackStrategy. */
@Singleton
public class LavaplayerPlaybackStrategy extends AudioEventAdapter implements PlaybackStrategy {
  private static final Logger logger = LoggerFactory.getLogger(LavaplayerPlaybackStrategy.class);

  private final AudioPlayerManager playerManager;
  private final ConcurrentMap<Long, AudioPlayer> guildPlayers = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, LavaplayerAudioSendHandler> guildSendHandlers =
      new ConcurrentHashMap<>();
  private AudioController audioController;
  private JDA jda;

  @Inject
  public LavaplayerPlaybackStrategy(LavaplayerSearchService searchService) {
    this.playerManager = searchService.getPlayerManager();
  }

  public void setAudioController(AudioController audioController) {
    this.audioController = audioController;
  }

  public void setJDA(JDA jda) {
    this.jda = jda;
  }

  @Override
  public void startPlayback(long guildId, dev.cafe.audio.AudioTrack track) {
    AudioPlayer player = getOrCreatePlayer(guildId);

    if (track instanceof LavaplayerAudioTrack) {
      LavaplayerAudioTrack lavaTrack = (LavaplayerAudioTrack) track;
      AudioTrack clonedTrack = lavaTrack.getLavaTrack().makeClone();
      player.playTrack(clonedTrack);
      logger.info("Started playback for guild {}: {}", guildId, track.getTitle());
    } else {
      logger.error("Track is not a LavaplayerAudioTrack: {}", track.getClass());
    }
  }

  @Override
  public void stopPlayback(long guildId) {
    AudioPlayer player = guildPlayers.get(guildId);
    if (player != null) {
      player.stopTrack();
      logger.info("Stopped playback for guild {}", guildId);
    }
  }

  @Override
  public void pausePlayback(long guildId) {
    AudioPlayer player = guildPlayers.get(guildId);
    if (player != null) {
      player.setPaused(true);
      logger.info("Paused playback for guild {}", guildId);
    }
  }

  @Override
  public void resumePlayback(long guildId) {
    AudioPlayer player = guildPlayers.get(guildId);
    if (player != null) {
      player.setPaused(false);
      logger.info("Resumed playback for guild {}", guildId);
    }
  }

  @Override
  public void setVolume(long guildId, int volume) {
    AudioPlayer player = guildPlayers.get(guildId);
    if (player != null) {
      player.setVolume(Math.max(0, Math.min(100, volume)));
      logger.info("Set volume to {} for guild {}", volume, guildId);
    }
  }

  @Override
  public boolean isPlaying(long guildId) {
    AudioPlayer player = guildPlayers.get(guildId);
    return player != null && player.getPlayingTrack() != null && !player.isPaused();
  }

  @Override
  public boolean isPaused(long guildId) {
    AudioPlayer player = guildPlayers.get(guildId);
    return player != null && player.getPlayingTrack() != null && player.isPaused();
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason.mayStartNext && audioController != null) {
      // Find which guild this player belongs to
      for (var entry : guildPlayers.entrySet()) {
        if (entry.getValue() == player) {
          audioController.onTrackEnd(entry.getKey());
          break;
        }
      }
    }
  }

  public AudioPlayer getPlayer(long guildId) {
    return guildPlayers.get(guildId);
  }

  private AudioPlayer getOrCreatePlayer(long guildId) {
    return guildPlayers.computeIfAbsent(
        guildId,
        k -> {
          AudioPlayer player = playerManager.createPlayer();
          player.addListener(this);

          // Create and set audio send handler
          LavaplayerAudioSendHandler sendHandler = new LavaplayerAudioSendHandler(player);
          guildSendHandlers.put(guildId, sendHandler);

          // Set the audio send handler on the guild's audio manager
          if (jda != null) {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
              AudioManager audioManager = guild.getAudioManager();
              audioManager.setSendingHandler(sendHandler);
            }
          }

          return player;
        });
  }

  public LavaplayerAudioSendHandler getSendHandler(long guildId) {
    return guildSendHandlers.get(guildId);
  }
}
