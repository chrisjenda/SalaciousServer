package SalaciousServer.network.discordbot;

import SalaciousServer.network.discordbot.Listeners.CommandCleanupListener;
import SalaciousServer.network.discordbot.Listeners.DiscordListener;
import SalaciousServer.network.discordbot.commands.*;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.api.entity.webhook.WebhookBuilder;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.DiscordSender;
import zombie.network.GameServer;

public class DiscordBot {
  private final DiscordSender sender;
  private static DiscordApi api;
  private static Channel currentChannel;
  private static Webhook webhook;
  private static int playersOnline;

  public DiscordBot(DiscordSender s) {
    sender = s;
  }

  public static Channel getCurrentChannel() {
    return currentChannel;
  }

  // This is the main entrypoint for the discord bot and is called last from
  // GameServer.startServer()
  public void connect(boolean enabled, String token, String channelname, String channelid) {

    // Make sure that a token is Provided
    if (token == null || token.isEmpty()) {
      if (enabled) DebugLog.log(DebugType.Network, "DISCORD: ERROR: token not configured");
      enabled = false;
    }

    // Display Output if Discordbot is disabled in settings
    if (!enabled) {
      DebugLog.log(DebugType.Network, "*** DISCORD DISABLED ****");
    } else {
      // DiscordBot is Enabled so create the Javacord API
      api =
          new DiscordApiBuilder()
              .setUserCacheEnabled(true)
              .setToken(token)
              .setAllNonPrivilegedIntents()
              .addIntents(Intent.GUILD_PRESENCES, Intent.GUILD_MEMBERS)
              .login()
              .join();

      // Register commands
      CommandHandler handler = new JavacordHandler(api);

      handler.registerCommand(new InfoCommand());
      handler.registerCommand(new gettimeCommand(handler));
      handler.registerCommand(new saveCommand(handler));
      handler.registerCommand(new quitCommand(handler));
      handler.registerCommand(new backupCommand(handler));
      handler.registerCommand(new timecontrolCommand(handler));
      handler.registerCommand(new adminmsgCommand(handler));
      handler.registerCommand(new privatemsgCommand(handler));
      handler.registerCommand(new rmzombiesCommand(handler));

      // Add Listeners
      api.addMessageDeleteListener(new CommandCleanupListener());
      api.addMessageCreateListener(new DiscordListener(handler, sender));

      DebugLog.log(DebugType.Network, "*** DISCORD ENABLED ****");

      // Sets Monitoring Channel with either provided channelname or id
      if (!setChannel(channelname, channelid))
        return; // Stop DiscordBot if we Fail to Set Monitoring Channel

      DebugLog.log(
          DebugType.Network,
          "DISCORD: Monitoring Channel: "
              + currentChannel.toString()
              + " on Server: "
              + api.getServers().iterator().next().getName());
      // TODO: Make Command to get Invite Link or Only Display if not already invited
      DebugLog.log(
          DebugType.Network,
          "DISCORD: You can invite me by using the following url: " + api.createBotInvite());
      DebugLog.log(DebugType.Network, "*** DISCORD API CONNECTED ****");

      // Update the Bot's Name
      api.updateUsername(GameServer.ServerName);
      DebugLog.log(
          DebugType.Network, "DISCORD: Setting Discord Bot Name to: " + GameServer.ServerName);

      // Update Bot's Avatar
      try {
        // TODO Find Higher Res Static Link for Icon
        api.updateAvatar(
                new URL(
                    "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/apps/108600/2bd4642ae337e378e7b04a19d19683425c5f81a4.jpg"))
            .join();
      } catch (Exception e) {
        throw new RuntimeException("Bad URL when updating Discord Bot Avatar: \n" + e.getMessage());
      }

      // Set Default Activity
      api.updateActivity(ActivityType.PLAYING, "PZ with nobody");

      DebugLog.log(DebugType.Network, "DISCORD: Initializing Webhooks");
      // Loop over Existing Web Hooks and Add ours if not present
      currentChannel
          .asTextChannel()
          .ifPresent(
              textChannel -> {
                List<Webhook> webhooks = textChannel.getWebhooks().join();
                boolean webhookinit = false;
                for (Webhook wb : webhooks) {
                  if (wb.getName().orElse("").contains("PZdiscordWH")) {
                    webhook = wb;
                    webhookinit = true;
                    break;
                  }
                }

                // Webhook was not found so create it now
                if (!webhookinit) {
                  try {
                    webhook =
                        new WebhookBuilder(currentChannel.asServerTextChannel().orElseThrow())
                            .setName("PZdiscordWH")
                            .create()
                            .join();
                    DebugLog.log(
                        DebugType.Network,
                        "DISCORD: Created Webhook with Name: "
                            + webhook.getName()
                            + " ID: "
                            + webhook.getId());
                  } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                  }
                } else {
                  DebugLog.log(
                      DebugType.Network,
                      "DISCORD: Found Existing Webhook with Name: "
                          + webhook.getName()
                          + " ID: "
                          + webhook.getId());
                }
              });

      // Check that the Bot is Initialized
      if (currentChannel.asTextChannel().isPresent()) {
        DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION SUCCEEDED ****");
      } else {
        DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION FAILED ****");
      }
    }
  }

  public static void onplayerConnected() {
    playersOnline++;
    if (playersOnline == 1)
      api.updateActivity(ActivityType.PLAYING, "PZ with " + playersOnline + " Player");
    else api.updateActivity(ActivityType.PLAYING, "PZ with " + playersOnline + " Players");
  }

  public static void onplayerDisconnect() {
    playersOnline--;
    if (playersOnline == 1)
      api.updateActivity(ActivityType.PLAYING, "PZ with " + playersOnline + " Player");
    else if (playersOnline == 0) api.updateActivity(ActivityType.PLAYING, "PZ with nobody");
    else api.updateActivity(ActivityType.PLAYING, "PZ with " + playersOnline + " Players");
  }

  // Sets current chanel locals and calls setChannelByID or setChannelByName if ID is not avail
  private static boolean setChannel(String channelname, String channelid) {

    // If provided with a channel name then find the channel and add it to current
    if (channelname != null && !channelname.isEmpty()) {
      Collection<Channel> channels = api.getChannelsByName(channelname);
      currentChannel = channels.iterator().next();
      return true;
    } else if (channelid != null && !channelid.isEmpty()) {
      // Provided a channel id and we dont have a name so add channel by its ID
      Optional<Channel> channel = api.getChannelById(channelid);
      currentChannel = channel.orElseThrow();
      return true;
    }

    // Could not add Channel, Print Error
    if (currentChannel == null)
      DebugLog.log(DebugType.Network, "DISCORD: ERROR: channelname or channelid not configured");

    return false;
  }

  public void sendMessage(String player, String message) {
    // Send a message with Webhook if Player Username is Detected in Discord or otherwise as Bot
    DebugLog.log(
        DebugType.Network,
        "DISCORD: sendMessage, User '" + player + "' send message: '" + message + "'");
    if (currentChannel != null) {
      currentChannel
          .asServerChannel()
          .ifPresent(
              channel -> {
                // Try to find the Discord user with the in game player name.
                User discordUser =
                    channel.getServer().getMembersByDisplayNameIgnoreCase(player).iterator().next();
                if (discordUser != null) {
                  // If user is found then spoof the author of the message with a
                  // webhook
                  Optional<IncomingWebhook> wb = webhook.asIncomingWebhook();
                  wb.ifPresent(
                      incomingWebhook ->
                          new WebhookMessageBuilder()
                              .setContent(message)
                              .setDisplayAuthor(discordUser)
                              .send(incomingWebhook));
                } else {
                  // If user is not found send a normal discord message from the
                  // bot
                  DebugLog.log(
                      DebugType.Network,
                      "SALDISCORD: Unable to find member by name: "
                          + player
                          + " Sending Message as Bot");
                  currentChannel
                      .asTextChannel()
                      .ifPresent(textChannel -> textChannel.sendMessage(player + ": " + message));
                  DebugLog.log(
                      DebugType.Network,
                      "DISCORD: User '" + player + "' send message: '" + message + "'");
                }
              });
    }
  }
}
