package SalaciousServer.network.discordbot.Listeners;

import SalaciousServer.network.discordbot.DiscordBot;
import de.btobastian.sdcf4j.CommandHandler;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.DiscordSender;

public class DiscordListener implements MessageCreateListener {

  private final CommandHandler commandHandler;
  private final DiscordSender sender;

  public DiscordListener(CommandHandler commandHandler, DiscordSender sender) {
    this.commandHandler = commandHandler;
    this.sender = sender;
  }

  @Override
  public void onMessageCreate(MessageCreateEvent event) {
    // Filter message to only process messages on the monitored channel and not from any bots
    Channel currentChannel = DiscordBot.getCurrentChannel();
    if (currentChannel == null) return;
    if (event.getChannel().getId() != currentChannel.getId()) return;
    if (event.getMessageAuthor().isBotUser()) return;

    // Check if Message is a command and Process
    boolean messageIsCommand =
        commandHandler.getCommands().stream()
            .flatMap(command -> Arrays.stream(command.getCommandAnnotation().aliases()))
            .anyMatch(alias -> event.getMessageContent().startsWith(alias));

    // Send message to server if not a command
    if (!messageIsCommand) {
      DebugLog.log(
          DebugType.Network,
          "DISCORD: send message = \""
              + event.getMessage().getContent()
              + "\" for "
              + event.getMessage().getAuthor().getName()
              + ")");
      String message = this.replaceChannelIDByItsName(event.getApi(), event.getMessage());
      message = this.removeSmilesAndImages(message);
      if (!message.isEmpty() && !message.matches("^\\s$")) {
        sender.sendMessageFromDiscord(event.getMessage().getAuthor().getName(), message);
      }
    }
  }

  private String replaceChannelIDByItsName(DiscordApi api, Message message) {
    String messageContent = message.getContent();
    Pattern pattern = Pattern.compile("<#(\\d+)>");
    Matcher matcher = pattern.matcher(message.getContent());
    if (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); ++i) {
        Optional<Channel> channel = api.getChannelById(matcher.group(i));
        if (channel.isPresent()) {
          messageContent =
              messageContent.replaceAll(
                  "<#" + matcher.group(i) + ">", "#" + channel.get().getIdAsString());
        }
      }
    }
    return messageContent;
  }

  private String removeSmilesAndImages(String message) {
    StringBuilder stringBuilder = new StringBuilder();
    char[] chars = message.toCharArray();
    for (Character Char : chars) {
      if (!Character.isLowSurrogate(Char) && !Character.isHighSurrogate(Char)) {
        stringBuilder.append(Char);
      }
    }
    return stringBuilder.toString();
  }
}
