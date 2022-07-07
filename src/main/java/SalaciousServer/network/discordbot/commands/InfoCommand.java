package SalaciousServer.network.discordbot.commands;

import SalaciousServer.network.discordbot.DiscordBot;
import SalaciousServer.network.discordbot.DiscordConstants;
import SalaciousServer.network.discordbot.Listeners.CommandCleanupListener;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.Javacord;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

public class InfoCommand implements CommandExecutor {

  /**
   * Executes the {@code !info} command.
   *
   * @param server The server where the command was issued.
   * @param channel The channel where the command was issued.
   * @param message The message the command was issued in.
   */
  @Command(aliases = "!info", async = true, description = "Shows information about this bot")
  public void handleCommand(Server server, TextChannel channel, Message message) {
    if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
      return;
    }

    final DiscordApi api = channel.getApi();
    EmbedBuilder embed =
        new EmbedBuilder()
            .setColor(DiscordConstants.JAVACORD_ORANGE)
            .setTitle(api.getYourself().getName() + " - Zomboid Bot")
            .setThumbnail(api.getYourself().getAvatar())
            .setDescription(
                "Custom Bot for Project Zomboid by MrSalacious\n\n"
                    + "Powered by Javacord and sdcf4j")
            .addInlineField("GitHub", "https://github.com/chrisjenda/PZHook_server")
            .addInlineField("Javacord Version", Javacord.VERSION)
            .addInlineField("sdcf4j Version", "v1.0.10");

    CommandCleanupListener.insertResponseTracker(embed, message.getId());
    channel.sendMessage(embed).join();
  }
}
