package SalaciousServer.intercepts;

import SalaciousServer.intercepts.interceptify.annotations.InterceptClass;
import SalaciousServer.intercepts.interceptify.annotations.OverwriteConstructor;
import SalaciousServer.intercepts.interceptify.annotations.OverwriteMethod;
import java.lang.reflect.Method;
import zombie.network.DiscordBot;
import zombie.network.DiscordSender;

@InterceptClass("zombie.network.DiscordBot")
public class DiscordBotHook {

  public static SalaciousServer.network.discordbot.DiscordBot newDiscordBot;

  @OverwriteConstructor()
  public static void DiscordBot(DiscordBot instance, String ignoredServerName, DiscordSender s) {
    newDiscordBot = new SalaciousServer.network.discordbot.DiscordBot(s);
  }

  @OverwriteMethod("connect")
  public static void connect(
      DiscordBot instance,
      Method parent,
      boolean enabled,
      String token,
      String channelname,
      String channelid) {
    // Init and forward the connection request to the new Discord bot
    newDiscordBot.connect(enabled, token, channelname, channelid);
  }

  @OverwriteMethod("sendMessage")
  public static void _sendmessage(
      DiscordBot instance, Method parent, String player, String message) {
    newDiscordBot.sendMessage(player, message);
  }
}
