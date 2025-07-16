Project Structure & Setup (Epic: Foundation)
This epic covers the initial setup of the project structure and the core components required for the bot to function.
User Story 2: As a developer, I want to define the common interfaces so that all microservices can communicate consistently.
    Task 2.1: In bot-common/, define a Song data class with attributes: title, url, thumbnail_url, duration, requester_id, and guild_id.
    Task 2.2: In bot-common/, define a Queue interface with methods like add_song, get_next_song, remove_song, get_queue, and clear_queue.
    Task 2.3: In bot-common/, define a PlayerStatus enum with states like PLAYING, PAUSED, and STOPPED.
User Story 3: As a developer, I want to configure the infrastructure so that the services can run in a containerized environment.
    Task 3.1: In infra-config/, create a Dockerfile for bot-core.
    Task 3.2: In infra-config/, create a Dockerfile for bot-app.
    Task 3.3: In infra-config/, create a Dockerfile for bot-player.
    Task 3.4: In infra-config/, set up a RabbitMQ service in the docker-compose.yml file.
    Task 3.5: In infra-config/, set up a PostgreSQL or a simple file-based database service (like SQLite) in the docker-compose.yml for persistence.

Bot Initialization & Configuration (Epic: First Run Experience)
This epic focuses on the bot's first interaction with a guild and the setup of its dedicated channel.
User Story 4: As a new user, I want the bot to guide me through the setup process when it joins my server so that I can easily configure its dedicated channel.
    Task 4.1: In bot-app/, implement an on_guild_join event handler.
    Task 4.2: In the on_guild_join event, send a direct message to the guild owner.
    Task 4.3: The DM should request the owner to specify a text channel for the bot's use (e.g., by mentioning the channel).
    Task 4.4: Implement a mechanism to listen for the owner's response in the DM.
    Task 4.5: Upon receiving a valid channel mention, store the guild_id and the designated channel_id in the database.
User Story 5: As a server administrator, I want the bot to prompt for setup if it's already in the server but hasn't been configured.
    Task 5.1: In bot-app/, on bot startup, iterate through all guilds the bot is a member of.
    Task 5.2: For each guild, check the database to see if a text channel has been configured.
    Task 5.3: If a guild is not configured, trigger the same DM setup flow as in User Story 4.
User Story 6: As a user, I want to see dedicated embeds for the queue and the currently playing song in the configured channel.
    Task 6.1: Once the text channel is configured, the bot should post two initial embed messages to that channel.
    Task 6.2: The top embed will be for the queue. It should initially show "The queue is empty."
    Task 6.3: The bottom embed will be for the "Now Playing" status. It should initially show "Nothing is currently playing."
    Task 6.4: Store the message IDs of these two embeds in the database, linked to the guild_id, for future updates.

The Player (Epic: Core Music Playback)
This epic covers the functionality of the /play command and the music playback system.
User Story 7: As a user, I want to be able to add a song to the queue using a search query or a URL.
    Task 7.1: In bot-core/, implement the /play slash command using discord.py's command integration.
    Task 7.2: The query option for the command should be a required string.
    Task 7.3: The command's response should be set to ephemeral, so only the user who typed it sees the initial confirmation.
    Task 7.4: Implement logic to determine if the user is in a voice channel. If not, respond with an ephemeral message asking them to join one.
    Task 7.5: Use yt-dlp to search for the query on YouTube if it's not a valid URL.
    Task 7.6: If the query is a playlist URL, extract all video information from the playlist.
    Task 7.7: For each song found, create a Song object (from bot-common) and add it to a list.
User Story 8: As a developer, I want to use a message queue to send songs to the player to prevent stuttering.
    Task 8.1: In bot-core/, after fetching the song(s), publish a message to a RabbitMQ queue (e.g., song_requests).
    Task 8.2: The message should contain the Song object(s) and the guild_id.
    Task 8.3: In bot-player/, create a consumer that listens to the song_requests queue.
    Task 8.4: When a message is received, the bot-player adds the song(s) to its internal queue for that guild.
User Story 9: As a user, I want the bot to join my voice channel and start playing when I add the first song.
    Task 9.1: In bot-player/, after a song is added to an empty queue, the bot should join the voice channel of the user who requested the song.
    Task 9.2: The bot-player will then start playing the first song in the queue.
    Task 9.3: Use yt-dlp to get the audio stream URL and play it using discord.py's FFmpegPCMAudio.
User Story 10: As a user, I want to see the "Now Playing" embed update with the current song's information.
    Task 10.1: When the bot-player starts playing a new song, it should publish a message to a RabbitMQ exchange (e.g., player_events) with the topic now_playing.update.
    Task 10.2: The message should contain the Song object and the guild_id.
    Task 10.3: In bot-core/, have a consumer for this topic.
    Task 10.4: Upon receiving the message, fetch the "Now Playing" embed message ID from the database for the corresponding guild.
    Task 10.5: Edit the embed to display the song's thumbnail, title, duration, a progress bar (can be text-based), and the user who requested it.
User Story 11: As a user, I want the queue embed to be updated when I add a song.
    Task 11.1: After adding a song to the queue in bot-player/, publish a message to the player_events exchange with the topic queue.update.
    Task 11.2: The message should contain the updated queue (or just the new song) and the guild_id.
    Task 11.3: In bot-core/, have a consumer for this topic.
    Task 11.4: Fetch the queue embed message ID from the database.
    Task 11.5: Edit the embed to display the updated queue with pagination (10 songs per page).
    Task 11.6: Add pagination buttons (e.g., ◀️, ▶️) to the embed message.
    Task 11.7: Add a red 'X' emoji reaction next to each song in the queue embed for deletion.
User Story 12: As a user, I want to be notified that the bot will leave the channel soon if the queue is empty.
    Task 12.1: In bot-player/, when the queue becomes empty but the bot is still in a voice channel, start a 30-minute timer.
    Task 12.2: In bot-core/, update the "Now Playing" embed to show a flash message: "The queue is empty. I will leave the channel in 30 minutes."
    Task 12.3: If a new song is added before the timer finishes, cancel the timer and remove the flash message.
    Task 12.4: If the timer finishes, the bot should leave the voice channel.

Playback Control (Epic: User Commands)
This epic details the user-facing commands for managing the music queue.
User Story 13: As a user, I want to be able to skip the currently playing song.
    Task 13.1: In bot-core/, implement the /skip slash command.
    Task 13.2: When the command is used, publish a message to a RabbitMQ queue (e.g., player_control) with the command skip and the guild_id.
    Task 13.3: In bot-player/, have a consumer for the player_control queue.
    Task 13.4: Upon receiving a skip command, stop the current song and play the next one in the queue.
    Task 13.5: If the queue is empty after skipping, follow the logic in User Story 12.
User Story 14: As a user, I want to be able to remove a specific song from the queue.
    Task 14.1: In bot-core/, implement an on_raw_reaction_add event handler.
    Task 14.2: Check if the reaction is the 'X' emoji and if it's on the queue embed message.
    Task 14.3: If so, determine which song the reaction corresponds to (based on its position in the embed).
    Task 14.4: Publish a message to the player_control queue with the command remove_song, the song's identifier, and the guild_id.
    Task 14.5: In bot-player/, handle the remove_song command by removing the specified song from the queue.
    Task 14.6: Trigger the queue.update event to update the queue embed.
User Story 15: As a user, I want to make the bot leave the voice channel.
    Task 15.1: In bot-core/, implement the /leave slash command.
    Task 15.2: Publish a message to the player_control queue with the command leave and the guild_id.
    Task 15.3: In bot-player/, upon receiving the leave command, disconnect from the voice channel.
    Task 15.4: Clear the queue for that guild.
User Story 16: As a user, I want to be able to completely reset the bot's state for my server.
    Task 16.1: In bot-core/, implement the /reset slash command.
    Task 16.2: Publish a message to the player_control queue with the command reset and the guild_id.
    Task 16.3: In bot-player/, upon receiving the reset command, disconnect from the voice channel and clear the queue.
    Task 16.4: In bot-core/, in response to the reset command, clear the queue embed and reset the "Now Playing" embed to their initial states.

## Done
User Story 1: As a developer, I want to set up the project's directory structure so that the codebase is modular and organized. - 651ce1e
    Task 1.1: Create the root directory for the project. - 651ce1e
    Task 1.2: Inside the root, create the following directories: bot-core/, bot-app/, bot-player/, bot-common/, and infra-config/. - 651ce1e
    Task 1.3: Create a docker-compose.yml file in the root directory. - 651ce1e
    Task 1.4: Create a README.md file with a brief description of the project and each module. - 651ce1e