package dev.cafe.bot;

import dev.cafe.cache.guild.GuildSettingsRepository;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/** Handles the /setup command for guild-specific configuration. */
@Singleton
public class SetupCommands extends ListenerAdapter {

  private final GuildSettingsRepository guildSettingsRepository;

  @Inject
  public SetupCommands(GuildSettingsRepository guildSettingsRepository) {
    this.guildSettingsRepository = guildSettingsRepository;
  }

  @Override
  public void onReady(ReadyEvent event) {
    event
        .getJDA()
        .updateCommands()
        .addCommands(
            Commands.slash("setup", "Set up the bot for this server.")
                .setDefaultPermissions(
                    DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true))
        .queue();
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("setup")) {
      return;
    }

    if (event.getGuild() == null) {
      // Should not happen due to setGuildOnly(true), but as a safeguard.
      event.reply("This command can only be used in a server.").setEphemeral(true).queue();
      return;
    }

    event.reply("Check your DMs to continue setup.").setEphemeral(true).queue();
  }
}
