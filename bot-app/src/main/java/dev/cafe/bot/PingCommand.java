package dev.cafe.bot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic ping command to verify bot functionality.
 */
public class PingCommand extends ListenerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(PingCommand.class);

  @Override
  public void onReady(ReadyEvent event) {
    event.getJDA().updateCommands().addCommands(
        Commands.slash("ping", "Check if the bot is responsive")
    ).queue();
    logger.info("Ping command registered");
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getName().equals("ping")) {
      long gatewayPing = event.getJDA().getGatewayPing();
      event.reply("Pong! Gateway ping: " + gatewayPing + "ms").queue();
      logger.info("Ping command executed with {}ms latency", gatewayPing);
    }
  }
}