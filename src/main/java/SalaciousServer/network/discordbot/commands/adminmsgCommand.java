package SalaciousServer.network.discordbot.commands;

import SalaciousServer.network.discordbot.DiscordBot;
import SalaciousServer.network.discordbot.DiscordConstants;
import SalaciousServer.network.discordbot.Listeners.CommandCleanupListener;
import com.google.common.base.Joiner;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import zombie.network.GameServer;

/** The !adminmsg command which is used to list all commands. */
public class adminmsgCommand implements CommandExecutor {

  private final CommandHandler commandHandler;

  /**
   * Initializes the command.
   *
   * @param commandHandler The command handler used to extract command usages and descriptions.
   */
  public adminmsgCommand(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  /**
   * Executes the {@code !adminmsg} command.
   *
   * @param api The Discord api.
   * @param server The server where the command was issued.
   * @param channel The channel where the command was issued.
   * @param message The message triggering the command.
   * @param args The command's arguments.
   */
  @Command(
      aliases = {"!adminmsg"},
      async = true,
      description = "Sends a message as admin to the server",
      usage = "!adminmsg <message>")
  public void onCommand(
      DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
    if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
      return;
    }

    if (args.length == 0) {
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle("Error")
              .setDescription("The `!adminmsg` command requires a message input")
              .setColor(DiscordConstants.ERROR_COLOR);
      CommandCleanupListener.insertResponseTracker(embed, message.getId());
      channel.sendMessage(embed).join();
      return;
    }

    EmbedBuilder embed =
        new EmbedBuilder()
            // .setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"),
            // "png")
            .setTitle("Admin Message")
            .setColor(DiscordConstants.JAVACORD_ORANGE);

    GameServer.sendAdminMessage(
        "Admin "
            + message.getAuthor().getDisplayName()
            + " Says: "
            + Joiner.on(", ").skipNulls().join(args).replace(",", ""),
        -1,
        -1,
        -1);
    embed.setDescription("Admin Message Sent");

    CommandCleanupListener.insertResponseTracker(embed, message.getId());
    channel.sendMessage(embed).join();
  }
}
