package de.gitterrost4.botlib.containers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.gitterrost4.botlib.helpers.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class PagedEmbed {
  Integer currentPage = 0;
  List<MessageEmbed> pages;
  private Message message;

  public PagedEmbed(List<MessageEmbed> pages) {
    if (pages.size() == 0) {
      throw new IllegalArgumentException("must have some pages");
    }
    this.pages = IntStream.range(0, pages.size())
        .mapToObj(i -> new EmbedBuilder(pages.get(i))
            .setAuthor(pages.get(i).getAuthor().getName() + " (" + (i + 1) + "/" + pages.size() + ")",
                pages.get(i).getAuthor().getUrl(), pages.get(i).getAuthor().getIconUrl())
            .build())
        .collect(Collectors.toList());
  }

  public PagedEmbed(MessageEmbed... pages) {
    this(Arrays.asList(pages));
  }

  public void handleReaction(MessageReactionAddEvent event) {
    message.removeReaction(event.getReactionEmote().getEmoji(), event.getUser()).queue();
    if (event.getReactionEmote().getEmoji().equals(Emoji.ARROW_DOUBLE_BACKWARD.asString())) {
      currentPage = 0;
    }
    if (event.getReactionEmote().getEmoji().equals(Emoji.ARROW_BACKWARD.asString())) {
      currentPage = ((currentPage - 1) % pages.size()+pages.size())%pages.size();
    }
    if (event.getReactionEmote().getEmoji().equals(Emoji.ARROW_FORWARD.asString())) {
      currentPage = (currentPage + 1) % pages.size();
    }
    if (event.getReactionEmote().getEmoji().equals(Emoji.ARROW_DOUBLE_FORWARD.asString())) {
      currentPage = pages.size() - 1;
    }
    update();
  }

  public String display(MessageChannel channel) {
    message = channel.sendMessageEmbeds(pages.get(currentPage)).complete(); // when creating, currentPage is always 0
    message.addReaction(Emoji.ARROW_DOUBLE_BACKWARD.asRepresentation()).queue();
    message.addReaction(Emoji.ARROW_BACKWARD.asRepresentation()).queue();
    message.addReaction(Emoji.ARROW_FORWARD.asRepresentation()).queue();
    message.addReaction(Emoji.ARROW_DOUBLE_FORWARD.asRepresentation()).queue();
    return message.getId();
  }

  private void update() {
    if (currentPage < 0 || currentPage >= pages.size()) {
      throw new IllegalStateException("index out of bounds");
    }
    message.editMessageEmbeds(pages.get(currentPage)).complete();
  }

  public void test6() {

  }

}
