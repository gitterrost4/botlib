package de.gitterrost4.botlib.containers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.gitterrost4.botlib.helpers.Emoji;
import de.gitterrost4.botlib.listeners.GlobalMenuListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class PagedEmbed {
  Integer currentPage = 0;
  List<MessageEmbed> pages;
  Predicate<ButtonClickEvent> accessControlPredicate = e->true;

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
  
  public void setAccessControl(Predicate<ButtonClickEvent> pred) {
    this.accessControlPredicate=pred;
  }

  public void handleButtonClick(ButtonClickEvent event) {
    if(!accessControlPredicate.test(event)) {
      event.reply("You are not allowed to do that.").setEphemeral(true).queue();
      return;
    }
    if (currentPage < 0 || currentPage >= pages.size()) {
      throw new IllegalStateException("index out of bounds");
    }
    switch(event.getComponentId()) {
    case "pagedEmbedFirst":
      currentPage = 0;
      break;
    case "pagedEmbedPrevious":
      currentPage = ((currentPage - 1) % pages.size()+pages.size())%pages.size();
      break;
    case "pagedEmbedNext":
      currentPage = (currentPage + 1) % pages.size();
      break;
    case "pagedEmbedLast":
      currentPage = pages.size() - 1;
      break;
    default: 
      break;
    }
    event.editMessageEmbeds(pages.get(currentPage)).complete();
  }
  
  public void display(MessageChannel channel) {
    Message message = channel.sendMessageEmbeds(pages.get(currentPage))
        .setActionRow(
            Button.secondary("pagedEmbedFirst", Emoji.ARROW_DOUBLE_BACKWARD.asEmoji()),
            Button.secondary("pagedEmbedPrevious", Emoji.ARROW_BACKWARD.asEmoji()),
            Button.secondary("pagedEmbedNext", Emoji.ARROW_FORWARD.asEmoji()),
            Button.secondary("pagedEmbedLast", Emoji.ARROW_DOUBLE_FORWARD.asEmoji())
            )
        .complete(); // when creating, currentPage is always 0
    
    GlobalMenuListener.register(message.getId(), this);
  }

}
