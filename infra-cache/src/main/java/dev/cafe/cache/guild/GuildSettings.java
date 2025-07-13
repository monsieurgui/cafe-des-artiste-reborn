package dev.cafe.cache.guild;

/** Holds settings for a specific guild. */
public record GuildSettings(long guildId, long channelId, long queueMsgId, long nowPlayingMsgId) {}
