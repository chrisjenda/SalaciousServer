package salaciousserver.network.discordbot.commands;

import salaciousserver.network.discordbot.DiscordBot;
import salaciousserver.network.discordbot.DiscordConstants;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCell;
import zombie.network.GameServer;
import salaciousserver.network.discordbot.Listeners.CommandCleanupListener;
import zombie.popman.ZombiePopulationManager;

@SuppressWarnings("unused")
public class rmzombiesCommand implements CommandExecutor {

    private final CommandHandler commandHandler;

    /**
     * @param commandHandler The command handler used to extract command usages and descriptions.
     */
    public rmzombiesCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * @param api The Discord api.
     * @param server  The server where the command was issued.
     * @param channel The channel where the command was issued.
     * @param message The message triggering the command.
     * @param args The command's arguments.
     */
    @Command(aliases = {"!rmzombies"}, async = true,
            description = "Removes all zombies in the provided players cell",
            usage = "!rmzombies <player> ")
    public void onCommand(DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
        if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
            return;
        }

        if (args.length < 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("rmzombies Commands requires a Player as input")
                    .setColor(DiscordConstants.ERROR_COLOR);
            CommandCleanupListener.insertResponseTracker(embed, message.getId());
            channel.sendMessage(embed).join();
            return;
        }

        IsoPlayer player = GameServer.getPlayerByUserName(args[1]);
        IsoCell playercell = player.getCell();
        ZombiePopulationManager.instance.dbgClearZombies(playercell.getWorldX(), playercell.getWorldY());

        EmbedBuilder embed = new EmbedBuilder()
                //.setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"), "png")
                .setTitle("Zombies removed around Player: " + player.getDisplayName() +
                        " in Cell: { X:" + playercell.getWorldX() + " Y: " + playercell.getWorldY() + " }")
                .setColor(DiscordConstants.JAVACORD_ORANGE);

        CommandCleanupListener.insertResponseTracker(embed, message.getId());
        channel.sendMessage(embed).join();
    }
}


