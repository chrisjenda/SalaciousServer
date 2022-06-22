package salaciousserver.network.discordbot.commands;

import salaciousserver.network.discordbot.DiscordBot;
import salaciousserver.network.discordbot.DiscordConstants;
import salaciousserver.network.discordbot.Listeners.CommandCleanupListener;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.MessageComponentInteraction;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.ServerMap;

public class quitCommand implements CommandExecutor {

    private final CommandHandler commandHandler;

    /**
     * Initializes the command.
     *
     * @param commandHandler The command handler used to extract command usages and descriptions.
     */
    public quitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Executes the {@code !quit} command.
     *
     * @param api The Discord api.
     * @param server  The server where the command was issued.
     * @param channel The channel where the command was issued.
     * @param message The message triggering the command.
     * @param args The command's arguments.
     */
    @Command(aliases = {"!quit"}, async = true, description = "Issues a safe Shutdown on the Server")
    public void onCommand(DiscordApi api, Server server, TextChannel channel, Message message, String[] args) {
        if (channel.getId() != DiscordBot.getCurrentChannel().getId()) {
            return;
        }

        if (args.length >= 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("The `!quit` command does not accept arguments!")
                    .setColor(DiscordConstants.ERROR_COLOR);
            CommandCleanupListener.insertResponseTracker(embed, message.getId());
            channel.sendMessage(embed).join();
            return;
        }

        // Add listener for Command Callbacks
        api.addMessageComponentCreateListener(event -> {
            MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
            String customId = messageComponentInteraction.getCustomId();
            DebugLog.log(DebugType.Network, "DISCORD: got quit command: " + customId);

            // Callback for Quit Button Confirmation for Server Shutdown Command
            if (customId.equals("quitserver")) {
                ServerMap.instance.QueueSaveAll();
                ServerMap.instance.QueueQuit();
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Saved World")
                        .setDescription("The Server is Safely Shutting Down")
                        .setColor(DiscordConstants.JAVACORD_ORANGE);
                CommandCleanupListener.insertResponseTracker(embed, message.getId());
                channel.sendMessage(embed).join();
                return;
            }
        });

        EmbedBuilder embed = new EmbedBuilder()
                //.setThumbnail(getClass().getClassLoader().getResourceAsStream("javacord3_icon.png"), "png")
                .setTitle("Current Game Time")
                .setColor(DiscordConstants.JAVACORD_ORANGE)
                .addField("ss", "Are you sure that you want to Quit?");

        CommandCleanupListener.insertResponseTracker(embed, message.getId());

        new MessageBuilder()
                .setContent("Are you Sure that you want to Quit?")
                .addEmbed(embed)
                .addComponents(ActionRow.of(Button.danger("quitserver", "QUIT SERVER")))
                .send(channel).join();
    }
}
