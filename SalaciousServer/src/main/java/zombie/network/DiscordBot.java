package zombie.network;

// Wrapper Class for DiscordBot interface
@SuppressWarnings("unused")
public class DiscordBot {
    public final salaciousserver.network.discordbot.DiscordBot newDiscordBot;

    // Hook Vanilla Discord bot Contructor and Init our Discord Bot
    public DiscordBot(String ignoredServerName, DiscordSender s) {
        newDiscordBot = new salaciousserver.network.discordbot.DiscordBot(s);
    }

    // This is the main entrypoint for the vanilla discord bot and is called last from GameServer.startServer()
    // Redirect the connection to the new bot
    public void connect(boolean enabled, String token, String channelname, String channelid) {
        // Init and forward the connection request to the new Discord bot
        newDiscordBot.connect(enabled, token, channelname, channelid);
    }

    // Forward In Game Messages to the new Discord bot
    public void sendMessage(String player, String message) {
        newDiscordBot.sendMessage(player, message);
    }

}
