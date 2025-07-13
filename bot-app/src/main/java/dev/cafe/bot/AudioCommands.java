package dev.cafe.bot;

import dev.cafe.audio.AudioTrack;
import dev.cafe.audio.SearchResult;
import dev.cafe.core.AudioController;
import dev.cafe.core.Playlist;
import dev.cafe.core.PlaylistManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Audio slash commands for music playback. */
public class AudioCommands extends ListenerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AudioCommands.class);

  private final AudioController audioController;
  private final PlaylistManager playlistManager;
  private final ConcurrentMap<String, List<AudioTrack>> searchCache = new ConcurrentHashMap<>();

  public AudioCommands(AudioController audioController, PlaylistManager playlistManager) {
    this.audioController = audioController;
    this.playlistManager = playlistManager;
  }

  @Override
  public void onReady(ReadyEvent event) {
    event
        .getJDA()
        .updateCommands()
        .addCommands(
            Commands.slash("join", "Join your voice channel"),
            Commands.slash("leave", "Leave the voice channel"),
            Commands.slash("play", "Play a song from YouTube or URL")
                .addOption(OptionType.STRING, "query", "Song name or URL", true)
                .addOption(
                    OptionType.BOOLEAN, "search", "Show search results to choose from", false),
            Commands.slash("search", "Search for tracks without playing")
                .addOption(OptionType.STRING, "query", "Song name to search for", true),
            Commands.slash("playlist", "Manage playlists")
                .addOption(OptionType.STRING, "action", "Action to perform", true)
                .addOption(OptionType.STRING, "name", "Playlist name", false)
                .addOption(OptionType.STRING, "id", "Playlist ID", false),
            Commands.slash("skip", "Skip the current song"),
            Commands.slash("stop", "Stop playback and clear queue"),
            Commands.slash("pause", "Pause the current song"),
            Commands.slash("resume", "Resume the paused song"),
            Commands.slash("queue", "Show the current queue"),
            Commands.slash("ping", "Check if the bot is responsive"))
        .queue();
    logger.info("Audio commands registered");
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getGuild() == null) {
      event.reply("This command can only be used in servers!").setEphemeral(true).queue();
      return;
    }

    long guildId = event.getGuild().getIdLong();

    switch (event.getName()) {
      case "join":
        handleJoin(event, guildId);
        break;
      case "leave":
        handleLeave(event, guildId);
        break;
      case "play":
        handlePlay(event, guildId);
        break;
      case "search":
        handleSearch(event, guildId);
        break;
      case "playlist":
        handlePlaylist(event, guildId);
        break;
      case "skip":
        handleSkip(event, guildId);
        break;
      case "stop":
        handleStop(event, guildId);
        break;
      case "pause":
        handlePause(event, guildId);
        break;
      case "resume":
        handleResume(event, guildId);
        break;
      case "queue":
        handleQueue(event, guildId);
        break;
      case "ping":
        handlePing(event);
        break;
    }
  }

  private void handlePlay(SlashCommandInteractionEvent event, long guildId) {
    String query = event.getOption("query").getAsString();
    boolean showSearch =
        event.getOption("search") != null && event.getOption("search").getAsBoolean();

    event.deferReply().queue();

    if (showSearch && !query.startsWith("http")) {
      // Show search results for user selection
      audioController
          .searchOnly(query)
          .thenAccept(
              result -> {
                if (result.getType() == dev.cafe.audio.SearchResult.Type.SEARCH_RESULT
                    && !result.getTracks().isEmpty()) {
                  showSearchResults(event, result, guildId);
                } else {
                  // Fallback to direct play
                  audioController
                      .play(guildId, query)
                      .thenAccept(playResult -> event.getHook().editOriginal(playResult).queue());
                }
              })
          .exceptionally(
              throwable -> {
                logger.error("Error searching tracks", throwable);
                event.getHook().editOriginal("Error searching: " + throwable.getMessage()).queue();
                return null;
              });
    } else {
      // Direct play
      audioController
          .play(guildId, query)
          .thenAccept(result -> event.getHook().editOriginal(result).queue())
          .exceptionally(
              throwable -> {
                logger.error("Error playing track", throwable);
                event
                    .getHook()
                    .editOriginal("Error playing track: " + throwable.getMessage())
                    .queue();
                return null;
              });
    }
  }

  private void handleSearch(SlashCommandInteractionEvent event, long guildId) {
    String query = event.getOption("query").getAsString();
    event.deferReply().queue();

    audioController
        .searchOnly(query)
        .thenAccept(result -> showSearchResults(event, result, guildId))
        .exceptionally(
            throwable -> {
              logger.error("Error searching tracks", throwable);
              event.getHook().editOriginal("Error searching: " + throwable.getMessage()).queue();
              return null;
            });
  }

  private void handlePlaylist(SlashCommandInteractionEvent event, long guildId) {
    String action = event.getOption("action").getAsString();
    long userId = event.getUser().getIdLong();

    switch (action.toLowerCase()) {
      case "create":
        String name =
            event.getOption("name") != null ? event.getOption("name").getAsString() : null;
        if (name == null || name.trim().isEmpty()) {
          event.reply("Please provide a playlist name!").setEphemeral(true).queue();
          return;
        }

        Playlist playlist = playlistManager.createPlaylist(userId, name.trim());
        event
            .reply(
                String.format(
                    "Created playlist '%s' with ID: `%s`", playlist.getName(), playlist.getId()))
            .setEphemeral(true)
            .queue();
        break;

      case "list":
        List<Playlist> userPlaylists = playlistManager.getUserPlaylists(userId);
        if (userPlaylists.isEmpty()) {
          event
              .reply(
                  "You don't have any playlists. Use `/playlist action:create name:My Playlist` to create one!")
              .setEphemeral(true)
              .queue();
          return;
        }

        EmbedBuilder embed = new EmbedBuilder().setTitle("🎵 Your Playlists").setColor(0x1DB954);

        for (Playlist pl : userPlaylists) {
          embed.addField(
              pl.getName(),
              String.format(
                  "**%d tracks** • ID: `%s`\nCreated: <t:%d:R>",
                  pl.size(), pl.getId(), pl.getCreatedAt().getEpochSecond()),
              false);
        }

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        break;

      case "load":
        String playlistId =
            event.getOption("id") != null ? event.getOption("id").getAsString() : null;
        if (playlistId == null) {
          event.reply("Please provide a playlist ID!").setEphemeral(true).queue();
          return;
        }

        String result = playlistManager.loadPlaylistToQueue(playlistId, audioController, guildId);
        event.reply(result).queue();
        break;

      case "show":
        String showId = event.getOption("id") != null ? event.getOption("id").getAsString() : null;
        if (showId == null) {
          event.reply("Please provide a playlist ID!").setEphemeral(true).queue();
          return;
        }

        playlistManager
            .getPlaylist(showId)
            .ifPresentOrElse(
                pl -> {
                  EmbedBuilder playlistEmbed =
                      new EmbedBuilder()
                          .setTitle("🎵 " + pl.getName())
                          .setDescription(
                              String.format(
                                  "**%d tracks** • Created by <@%d>", pl.size(), pl.getOwnerId()))
                          .setColor(0x1DB954);

                  List<AudioTrack> tracks = pl.getTracks();
                  int maxShow = Math.min(10, tracks.size());
                  StringBuilder trackList = new StringBuilder();

                  for (int i = 0; i < maxShow; i++) {
                    AudioTrack track = tracks.get(i);
                    trackList.append(
                        String.format(
                            "%d. **%s** - %s\n", i + 1, track.getTitle(), track.getAuthor()));
                  }

                  if (tracks.size() > maxShow) {
                    trackList.append(
                        String.format("... and %d more tracks", tracks.size() - maxShow));
                  }

                  if (!trackList.toString().isEmpty()) {
                    playlistEmbed.addField("Tracks", trackList.toString(), false);
                  }

                  event.replyEmbeds(playlistEmbed.build()).queue();
                },
                () -> {
                  event.reply("Playlist not found!").setEphemeral(true).queue();
                });
        break;

      default:
        event
            .reply(
                "Available actions: `create`, `list`, `load`, `show`\n"
                    + "Examples:\n"
                    + "• `/playlist action:create name:My Playlist`\n"
                    + "• `/playlist action:list`\n"
                    + "• `/playlist action:load id:playlist-id`\n"
                    + "• `/playlist action:show id:playlist-id`")
            .setEphemeral(true)
            .queue();
    }
  }

  private void handleSkip(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.skip(guildId);
    event.reply(result).queue();
  }

  private void handleStop(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.stop(guildId);
    event.reply(result).queue();
  }

  private void handlePause(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.pause(guildId);
    event.reply(result).queue();
  }

  private void handleResume(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.resume(guildId);
    event.reply(result).queue();
  }

  private void handleQueue(SlashCommandInteractionEvent event, long guildId) {
    String result = audioController.getQueueInfo(guildId);
    event.reply(result).queue();
  }

  private void handleJoin(SlashCommandInteractionEvent event, long guildId) {
    Member member = event.getMember();
    if (member == null
        || member.getVoiceState() == null
        || !member.getVoiceState().inAudioChannel()) {
      event.reply("You need to be in a voice channel!").setEphemeral(true).queue();
      return;
    }

    AudioChannel voiceChannel = member.getVoiceState().getChannel();
    AudioManager audioManager = event.getGuild().getAudioManager();

    audioManager.openAudioConnection(voiceChannel);
    event.reply("Joined " + voiceChannel.getName()).queue();
  }

  private void handleLeave(SlashCommandInteractionEvent event, long guildId) {
    AudioManager audioManager = event.getGuild().getAudioManager();

    if (!audioManager.isConnected()) {
      event.reply("I'm not connected to a voice channel!").setEphemeral(true).queue();
      return;
    }

    audioManager.closeAudioConnection();
    event.reply("Left the voice channel").queue();
  }

  private void handlePing(SlashCommandInteractionEvent event) {
    long gatewayPing = event.getJDA().getGatewayPing();
    event.reply("Pong! Gateway ping: " + gatewayPing + "ms").queue();
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    String buttonId = event.getComponentId();

    if (buttonId.startsWith("track_")) {
      String[] parts = buttonId.split("_");
      if (parts.length >= 3) {
        String searchId = parts[1];
        int trackIndex = Integer.parseInt(parts[2]);

        List<AudioTrack> tracks = searchCache.get(searchId);
        if (tracks != null && trackIndex < tracks.size()) {
          AudioTrack selectedTrack = tracks.get(trackIndex);
          long guildId = event.getGuild().getIdLong();

          String result = audioController.playSelectedTrack(guildId, selectedTrack);
          event.reply(result).setEphemeral(true).queue();

          // Disable the buttons after selection
          event.editComponents().queue();
        } else {
          event.reply("Search results expired. Please search again.").setEphemeral(true).queue();
        }
      }
    } else if (buttonId.startsWith("playlist_")) {
      String[] parts = buttonId.split("_");
      if (parts.length >= 3) {
        String searchId = parts[1];
        int trackIndex = Integer.parseInt(parts[2]);

        List<AudioTrack> tracks = searchCache.get(searchId);
        if (tracks != null && trackIndex < tracks.size()) {
          AudioTrack selectedTrack = tracks.get(trackIndex);
          showPlaylistSelection(event, selectedTrack);
        } else {
          event.reply("Search results expired. Please search again.").setEphemeral(true).queue();
        }
      }
    } else if (buttonId.startsWith("addtopl_")) {
      String[] parts = buttonId.split("_", 3);
      if (parts.length >= 3) {
        String playlistId = parts[1];
        String trackData = parts[2]; // This would need to be encoded track info

        // For simplicity, we'll handle this differently - store track in cache
        event
            .reply("Track would be added to playlist (feature needs track persistence)")
            .setEphemeral(true)
            .queue();
      }
    }
  }

  private void showSearchResults(
      SlashCommandInteractionEvent event, SearchResult result, long guildId) {
    if (result.getType() != SearchResult.Type.SEARCH_RESULT || result.getTracks().isEmpty()) {
      event.getHook().editOriginal("No search results found.").queue();
      return;
    }

    List<AudioTrack> tracks = result.getTracks();
    int maxResults = Math.min(5, tracks.size()); // Show max 5 results
    String searchId = String.valueOf(System.currentTimeMillis());

    // Cache the search results
    searchCache.put(searchId, tracks.subList(0, maxResults));

    EmbedBuilder embed =
        new EmbedBuilder()
            .setTitle("🔍 Search Results")
            .setDescription("Select a track to play:")
            .setColor(0x1DB954);

    List<Button> buttons = new ArrayList<>();

    for (int i = 0; i < maxResults; i++) {
      AudioTrack track = tracks.get(i);
      String duration = formatDuration(track.getDuration());

      embed.addField(
          String.format("%d. %s", i + 1, track.getTitle()),
          String.format("**%s** • %s", track.getAuthor(), duration),
          false);

      buttons.add(Button.primary(String.format("track_%s_%d", searchId, i), String.valueOf(i + 1)));

      // Add playlist button for every 2nd track to avoid too many buttons
      if (i % 2 == 1 && i < 4) {
        buttons.add(Button.secondary(String.format("playlist_%s_%d", searchId, i - 1), "➕"));
      }
    }

    // Remove cached results after 5 minutes
    event
        .getJDA()
        .getRestPing()
        .queue(
            ping -> {
              event
                  .getJDA()
                  .getRateLimitPool()
                  .schedule(
                      () -> {
                        searchCache.remove(searchId);
                      },
                      5,
                      java.util.concurrent.TimeUnit.MINUTES);
            });

    event.getHook().editOriginalEmbeds(embed.build()).setComponents(ActionRow.of(buttons)).queue();
  }

  private String formatDuration(Duration duration) {
    if (duration == null) return "Unknown";

    long seconds = duration.getSeconds();
    if (seconds < 0) return "Stream";

    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;

    if (hours > 0) {
      return String.format("%d:%02d:%02d", hours, minutes, secs);
    } else {
      return String.format("%d:%02d", minutes, secs);
    }
  }

  private void showPlaylistSelection(ButtonInteractionEvent event, AudioTrack track) {
    long userId = event.getUser().getIdLong();
    List<Playlist> userPlaylists = playlistManager.getUserPlaylists(userId);

    if (userPlaylists.isEmpty()) {
      event
          .reply(
              "You don't have any playlists! Create one with `/playlist action:create name:My Playlist`")
          .setEphemeral(true)
          .queue();
      return;
    }

    EmbedBuilder embed =
        new EmbedBuilder()
            .setTitle("➕ Add to Playlist")
            .setDescription(
                String.format(
                    "Select a playlist to add **%s** by %s", track.getTitle(), track.getAuthor()))
            .setColor(0x1DB954);

    List<Button> buttons = new ArrayList<>();
    for (int i = 0; i < Math.min(5, userPlaylists.size()); i++) {
      Playlist playlist = userPlaylists.get(i);
      buttons.add(
          Button.success(
              String.format("addtrack_%s_%s", playlist.getId(), System.currentTimeMillis()),
              String.format("%s (%d)", playlist.getName(), playlist.size())));

      // Store track temporarily for adding to playlist
      searchCache.put("track_" + System.currentTimeMillis(), List.of(track));
    }

    event
        .replyEmbeds(embed.build())
        .setComponents(ActionRow.of(buttons))
        .setEphemeral(true)
        .queue();
  }
}
