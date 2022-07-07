package SalaciousServer.network.discordbot.Listeners;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.event.message.MessageDeleteEvent;
import org.javacord.api.listener.message.MessageDeleteListener;

/** A listener to clean up our responses to user commands if the command was deleted. */
public class CommandCleanupListener implements MessageDeleteListener {

  /**
   * Inserts a tracking footer into an embed builder.
   *
   * @param builder The embed builder to change.
   * @param trackedMessageId The id of the message to track.
   * @return The embed builder for call chaining.
   */
  public static EmbedBuilder insertResponseTracker(EmbedBuilder builder, long trackedMessageId) {
    return builder.setFooter(
        longToBinaryBlankString(trackedMessageId)
            + "If you delete your invocation message, this response will be deleted.");
  }

  @Override
  public void onMessageDelete(MessageDeleteEvent event) {
    final Predicate<Message> isAfterAnHour =
        isMessageAfter(
            DiscordEntity.getCreationTimestamp(event.getMessageId()).plus(1L, ChronoUnit.HOURS));
    final Predicate<Message> isOurResponseToDeletedMessage = isOurResponseTo(event.getMessageId());
    event
        .getChannel()
        .getMessagesAfterUntil(
            isAfterAnHour.or(isOurResponseToDeletedMessage), event.getMessageId())
        .thenAccept(
            messages ->
                messages
                    .getNewestMessage()
                    .filter(isOurResponseToDeletedMessage)
                    .ifPresent(
                        message ->
                            message.delete("Triggering command message has" + " been deleted")));
  }

  private Predicate<Message> isMessageAfter(Instant instant) {
    return message -> message.getCreationTimestamp().isAfter(instant);
  }

  private Predicate<Message> isOurResponseTo(long messageId) {
    String tracker = longToBinaryBlankString(messageId);
    return message ->
        !message.getEmbeds().isEmpty()
            && message.getAuthor().isYourself()
            && message
                .getEmbeds()
                .get(0)
                .getFooter()
                .flatMap(EmbedFooter::getText)
                .filter(text -> text.startsWith(tracker))
                .isPresent();
  }

  private static String longToBinaryBlankString(long l) {
    return Long.toBinaryString(l).replace('0', '\u200B').replace('1', '\u200C') + '\u200D';
  }
}
