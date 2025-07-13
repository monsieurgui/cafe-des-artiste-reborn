package dev.cafe.bot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.PlaybackStrategy;
import dev.cafe.cache.guild.GuildSettings;
import dev.cafe.cache.guild.GuildSettingsRepository;
import dev.cafe.core.TrackQueue;
import java.time.Duration;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PostUpdaterTest {

  private static class TestAudioTrack implements AudioTrack {
    @Override
    public String getTitle() {
      return "Test Track";
    }

    @Override
    public String getAuthor() {
      return "Test Author";
    }

    @Override
    public String getUrl() {
      return "http://test.com";
    }

    @Override
    public String getVideoId() {
      return "test-id";
    }

    @Override
    public Duration getDuration() {
      return Duration.ofSeconds(123);
    }

    @Override
    public String getArtworkUrl() {
      return null;
    }

    @Override
    public boolean isSeekable() {
      return true;
    }

    @Override
    public Object getSourceTrack() {
      return null;
    }
  }

  @Mock private GuildSettingsRepository guildSettingsRepository;
  @Mock private JDA jda;
  @Mock private TextChannel textChannel;
  @Mock private Message message;
  @Mock private MessageEditAction messageEditAction;
  @Mock private PlaybackStrategy playbackStrategy;

  private PostUpdater postUpdater;
  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    postUpdater = new PostUpdater(guildSettingsRepository, playbackStrategy);
    postUpdater.setJda(jda);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void onQueueChanged_updatesMessage() {
    // Given
    long guildId = 1L;
    long channelId = 2L;
    long messageId = 3L;
    GuildSettings settings = new GuildSettings(guildId, channelId, messageId, 4L);
    TrackQueue queue = new TrackQueue();
    queue.add(new TestAudioTrack());

    when(guildSettingsRepository.findById(guildId)).thenReturn(Optional.of(settings));
    when(playbackStrategy.getCurrentTrack(guildId)).thenReturn(new TestAudioTrack());
    when(jda.getTextChannelById(channelId)).thenReturn(textChannel);
    when(textChannel.retrieveMessageById(messageId)).thenReturn(new RestActionMock<>(message));
    when(message.editMessageEmbeds(any(MessageEmbed.class))).thenReturn(messageEditAction);

    // When
    postUpdater.onQueueChanged(guildId, queue);

    // Then
    verify(message).editMessageEmbeds(any(MessageEmbed.class));
    verify(messageEditAction).queue();
  }
}
