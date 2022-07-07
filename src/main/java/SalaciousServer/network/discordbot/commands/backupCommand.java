package SalaciousServer.network.discordbot.commands;

import SalaciousServer.network.discordbot.DiscordBot;
import SalaciousServer.network.discordbot.DiscordConstants;
import SalaciousServer.network.discordbot.Listeners.CommandCleanupListener;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import zombie.core.backup.ZipBackup;

public class backupCommand implements CommandExecutor {

  private final CommandHandler commandHandler;

  /**
   * Initializes the command.
   *
   * @param commandHandler The command handler used to extract command usages and descriptions.
   */
  public backupCommand(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  /**
   * Executes the {@code !backup} command.
   *
   * @param api The Discord api.
   * @param server The server where the command was issued.
   * @param channel The channel where the command was issued.
   * @param message The message triggering the command.
   * @param args The command's arguments.
   */
  @Command(
      aliases = {"!backup"},
      async = true,
      description = "Starts a Server Backup")
  public void onCommand(
      DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
    if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
      return;
    }

    if (args.length >= 1) {
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle("Error")
              .setDescription("The `!backup` command does not accept arguments!")
              .setColor(DiscordConstants.ERROR_COLOR);
      CommandCleanupListener.insertResponseTracker(embed, message.getId());
      channel.sendMessage(embed).join();
      return;
    }

    EmbedBuilder embed =
        new EmbedBuilder()
            // .setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"),
            // "png")
            .setTitle("Server is Backed Up")
            .setColor(DiscordConstants.JAVACORD_ORANGE);

    ZipBackup.onStartup();

    CommandCleanupListener.insertResponseTracker(embed, message.getId());
    channel.sendMessage(embed).join();
  }
}
