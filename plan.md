Project Structure & Setup (Epic: Foundation)
This epic covers the initial setup of the Java project structure, build configurations, and core components.
User Story 1: As a developer, I want to set up a multi-module Maven/Gradle project so that the codebase is modular and organized.
Task 1.1: Create a root pom.xml (Maven) or settings.gradle (Gradle) file to define the project modules.
Task 1.2: Create the following modules: bot-core, bot-app, bot-player, bot-common, and infra-config.
Task 1.3: In the root, create a docker-compose.yml file.
Task 1.4: In infra-config, create Dockerfile templates for each Java-based microservice.
Task 1.5: Configure the build file in each module to include necessary dependencies (e.g., JDA in bot-app and bot-core, LavaPlayer in bot-player).
User Story 2: As a developer, I want to define the common interfaces and data models so that all microservices can communicate consistently.
Task 2.1: In the bot-common module, define a Song POJO (Plain Old Java Object) with attributes: title, url, thumbnailUrl, duration, requesterId, and guildId.
Task 2.2: In bot-common, define a QueueManager interface with methods like addSong(Song song), getNextSong(), removeSong(int index), getQueue(), and clearQueue().
Task 2.3: In bot-common, define a PlayerStatus enum with states: PLAYING, PAUSED, and STOPPED.
User Story 3: As a developer, I want to configure the infrastructure so that the services can run in a containerized environment.
Task 3.1: Configure the docker-compose.yml to build and run each Java microservice.
Task 3.2: Add a RabbitMQ service to the docker-compose.yml.
Task 3.3: Add a PostgreSQL or H2 (for simplicity) database service to docker-compose.yml for persistence.
Task 3.4: Configure environment variables in docker-compose.yml for the bot token, database credentials, and RabbitMQ connection details.

Bot Initialization & Configuration (Epic: First Run Experience)
This epic focuses on the bot's first interaction with a guild and setting up its dedicated channel.
User Story 4: As a new user, I want the bot to guide me through the setup process when it joins my server.
Task 4.1: In bot-app, implement a JDA ListenerAdapter to handle the onGuildJoin event.
Task 4.2: In the onGuildJoin event handler, retrieve the guild owner and open a private channel (DM) with them.
Task 4.3: The DM should ask the owner to specify a text channel for the bot by mentioning it (e.g., #music-commands).
Task 4.4: Implement logic to listen for the owner's response in the private channel.
Task 4.5: Upon receiving a valid channel mention, persist the guildId and the designated channelId to the database (using JDBC or a simple ORM).
User Story 5: As a server administrator, I want the bot to prompt for setup if it's already in the server but hasn't been configured.
Task 5.1: In bot-app, on bot startup (onReady event), iterate through all guilds the bot is a member of.
Task 5.2: For each guild, query the database to see if a text channel has been configured.
Task 5.3: If a guild is not configured, trigger the same DM setup flow as in User Story 4.
User Story 6: As a user, I want to see dedicated embeds for the queue and the "Now Playing" status.
Task 6.1: Once the text channel is configured, use JDA's MessageBuilder and EmbedBuilder to create and send two initial embed messages to that channel.
Task 6.2: The top embed will be for the queue, initially showing "The queue is empty."
Task 6.3: The bottom embed will be for "Now Playing," initially showing "Nothing is currently playing."
Task 6.4: Store the message IDs of these two embeds in the database, linked to the guildId, for future updates.

The Player (Epic: Core Music Playback)
This epic covers the functionality of the /play command and the music playback system.
User Story 7: As a user, I want to add a song to the queue using a search query or a URL.
Task 7.1: In bot-core, register a /play SlashCommand using JDA's command-updaters.
Task 7.2: The command should have a required query option of type String.
Task 7.3: Set the command's reply to be ephemeral (setEphemeral(true)).
Task 7.4: Check if the user is in a voice channel. If not, reply with an ephemeral error message.
Task 7.5: Publish a message to a RabbitMQ queue (e.g., song.request). The message body will contain the query, guildId, and the user's voiceChannelId.
User Story 8: As a developer, I want the player service to handle audio processing asynchronously.
Task 8.1: In bot-player, create a RabbitMQ consumer that listens to the song.request queue.
Task 8.2: The consumer will use LavaPlayer's AudioPlayerManager to load tracks. For search queries, prefix the query with ytsearch:.
Task 8.3: Implement the AudioLoadResultHandler to handle the results:
    trackLoaded: Add the single track to the queue.
    playlistLoaded: Add all tracks from the playlist to the queue.
    noMatches: Publish an event back to bot-core to notify the user.
    loadFailed: Log the error and notify the user.
Task 8.4: For each loaded song, create a Song object and add it to the guild's queue.
User Story 9: As a user, I want the bot to join my voice channel and start playing.
Task 9.1: In bot-player, after a song is added to a previously empty queue, get the guild's AudioManager.
Task 9.2: Open an audio connection to the voiceChannelId received in the message.
Task 9.3: Set the SendingHandler for the AudioManager to a LavaPlayerAudioSendHandler.
Task 9.4: Start playing the first track in the queue using the AudioPlayer's playTrack method.
User Story 10: As a user, I want to see the "Now Playing" embed update automatically.
Task 10.1: When the bot-player's AudioPlayer fires an OnTrackStart event, publish a message to a RabbitMQ topic (e.g., events.nowplaying.update).
Task 10.2: In bot-core, a consumer listening to this topic will receive the Song data.
Task 10.3: It will then fetch the "Now Playing" embed message ID from the database and edit the message to show the new song's thumbnail, title, duration, and requester.
User Story 11: As a user, I want a beautiful, interactive queue embed.
Task 11.1: Whenever the queue is modified in bot-player, publish a events.queue.update message.
Task 11.2: In bot-core, a consumer will update the queue embed.
Task 11.3: Implement pagination using JDA's ActionRow and Button components (e.g., Button.primary("prev_page", "◀️")).
Task 11.4: To allow deletion, add a button next to each song entry in the embed, or use reactions. Buttons are more modern. For example, Button.danger("delete_song_1", "X").
Task 11.5: Create a listener in bot-core for these button interactions to publish deletion commands.
User Story 12: As a user, I want the bot to leave automatically after a period of inactivity.
Task 12.1: In bot-player, when the onQueueEnd event occurs, start a ScheduledExecutorService timer for 30 minutes.
Task 12.2: Publish an event to bot-core to update the "Now Playing" embed with a "Will leave in 30 minutes" message.
Task 12.3: If a new track is queued before the timer finishes, cancel the scheduled task.
Task 12.4: If the timer completes, publish a leave command for itself.

Playback Control (Epic: User Commands)
This epic details the user-facing commands for managing the music queue.
User Story 13: As a user, I want to skip the current song.
Task 13.1: In bot-core, register a /skip slash command.
Task 13.2: When used, publish a message to a player.control queue with a skip command and the guildId.
Task 13.3: In bot-player, a consumer will handle the skip command by calling nextTrack() on the guild's track scheduler, which effectively stops the current track and starts the next.
User Story 14: As a user, I want to make the bot leave the voice channel.
Task 14.1: In bot-core, register a /leave slash command.
Task 14.2: Publish a leave command to the player.control queue.
Task 14.3: In bot-player, the consumer will close the audio connection (closeAudioConnection()) and clear the queue.
User Story 15: As a user, I want to completely reset the bot's state for my server.
Task 15.1: In bot-core, register a /reset slash command.
Task 15.2: Publish a reset command to the player.control queue.
Task 15.3: In bot-player, the handler will perform the same actions as leave.
Task 15.4: In bot-core, a listener for the reset event will also clear the embeds, resetting them to their initial "empty" state.

## DONE =====