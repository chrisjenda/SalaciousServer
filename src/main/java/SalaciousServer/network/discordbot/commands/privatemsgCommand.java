package SalaciousServer.network.discordbot.commands;

import SalaciousServer.network.discordbot.DiscordBot;
import SalaciousServer.network.discordbot.DiscordConstants;
import SalaciousServer.network.discordbot.Listeners.CommandCleanupListener;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Optional;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCell;
import zombie.network.GameServer;
import zombie.network.chat.ChatServer;
import zombie.popman.ZombiePopulationManager;

/** The !privatemsg command which is used to list all commands. */
public class privatemsgCommand implements CommandExecutor {

  private final CommandHandler commandHandler;

  /**
   * Initializes the command.
   *
   * @param commandHandler The command handler used to extract command usages and descriptions.
   */
  public privatemsgCommand(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  /**
   * Executes the {@code !privatemsg} command.
   *
   * @param api The Discord api.
   * @param server The server where the command was issued.
   * @param channel The channel where the command was issued.
   * @param message The message triggering the command.
   * @param args The command's arguments.
   */
  @Command(
      aliases = {"!privatemsg"},
      async = true,
      description = "Sends a private message to a player",
      usage = "!privatemsg <player> <message>")
  public void onCommand(
      DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
    if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
      return;
    }

    if (args.length < 2) {
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle("Error")
              .setDescription("Missing Arguments, type commands for more information")
              .setColor(DiscordConstants.ERROR_COLOR);
      CommandCleanupListener.insertResponseTracker(embed, message.getId());
      channel.sendMessage(embed).join();
      return;
    }

    Optional<Message> message2 = channel.getMessages(10000).join().getNewestMessage();
    message2.ifPresent(Message::delete);

    IsoPlayer player = GameServer.getPlayerByUserName(args[1]);
    IsoCell playercell = player.getCell();
    ZombiePopulationManager.instance.dbgClearZombies(
        playercell.getWorldX(), playercell.getWorldY());

    ByteBuffer var0 = ByteBuffer.allocate(1024);
    CharBuffer cbuf = var0.asCharBuffer();
    cbuf.put(message.getAuthor().getDisplayName());
    cbuf.put(args[0]);
    // cbuf.flip();
    ChatServer.getInstance().processPlayerStartWhisperChatPacket(var0);

    /*
           UdpEngine udpengine = GameServer.udpEngine;
           for(int i = 0; i < udpengine.connections.size(); ++i) {
               UdpConnection udp = (UdpConnection)udpengine.connections.get(i);
               IsoPlayer player = udp.players[i];

               System.out.println(udp.username.toLowerCase());
               if (udp.username.equals(args[0].toLowerCase())) {

                   // Build Packet Manually because Zomboid sucks
                   ByteBufferWriter buffer = udp.startPacket();
                   PacketTypes.PacketType.ChatMessageToPlayer.doPacket(buffer);

                   // Manually Pack Message since we cant access Packmessage
                   buffer.putInt(0);
                   buffer.putUTF(message.getAuthor().getDisplayName() );
                   buffer.putUTF(args[1]);


                   PacketTypes.PacketType.ChatMessageToPlayer.send(udp);


               }
           }

    */

    EmbedBuilder embed =
        new EmbedBuilder()
            // .setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"),
            // "png")
            .setColor(DiscordConstants.JAVACORD_ORANGE)
            .setDescription("Private Message Sent");

    CommandCleanupListener.insertResponseTracker(embed, message.getId());
    channel.sendMessage(embed).join();
  }
}
