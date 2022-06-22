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
import zombie.GameTime;
import salaciousserver.network.discordbot.Listeners.CommandCleanupListener;

public class gettimeCommand implements CommandExecutor {

    private final CommandHandler commandHandler;

    /**
     * Initializes the command.
     *
     * @param commandHandler The command handler used to extract command usages and descriptions.
     */
    public gettimeCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Executes the {@code !gettime} command.
     *
     * @param api The Discord api.
     * @param server  The server where the command was issued.
     * @param channel The channel where the command was issued.
     * @param message The message triggering the command.
     * @param args The command's arguments.
     */
    @Command(aliases = {"!gettime"}, async = true, description = "Returns the Time from In Game")
    public void onCommand(DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
        if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
            return;
        }

        if (args.length >= 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("The `!gettime` command does not accept arguments!")
                    .setColor(DiscordConstants.ERROR_COLOR);
            CommandCleanupListener.insertResponseTracker(embed, message.getId());
            channel.sendMessage(embed).join();
            return;
        }

        int y = GameTime.instance.getYear();
        int m = GameTime.instance.getMonth() + 1;
        int d = GameTime.instance.getDay() + 1;
        int h = GameTime.instance.getHour();
        int n = GameTime.instance.getMinutes();

        EmbedBuilder embed = new EmbedBuilder()
                //.setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"), "png")
                .setTitle("Current Game Time")
                .setColor(DiscordConstants.JAVACORD_ORANGE)
                .addField("Time", String.format("%04d-%02d-%02d %02d:%02d", y, m, d, h, n));


        CommandCleanupListener.insertResponseTracker(embed, message.getId());
        channel.sendMessage(embed).join();
    }
}
