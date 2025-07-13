package dev.cafe.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import java.nio.ByteBuffer;
import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * AudioSendHandler implementation for Lavaplayer that provides 20ms Opus frames to Discord.
 */
public class LavaplayerAudioSendHandler implements AudioSendHandler {
  private final AudioPlayer audioPlayer;
  private final ByteBuffer buffer;
  private final MutableAudioFrame frame;

  public LavaplayerAudioSendHandler(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
    this.buffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize());
    this.frame = new MutableAudioFrame();
    this.frame.setBuffer(buffer);
  }

  @Override
  public boolean canProvide() {
    return audioPlayer.provide(frame);
  }

  @Override
  public ByteBuffer provide20MsAudio() {
    return buffer.flip();
  }

  @Override
  public boolean isOpus() {
    return true;
  }
}