// $Id $
// (C) cantamen/Paul Kramer 2020
package de.gitterrost4.botlib.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.gitterrost4.botlib.helpers.Emoji;
import de.gitterrost4.botlib.listeners.GlobalMenuListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

/** 
 * TODO documentation
 *
 */
public class ChoiceMenu {
  private final String title;
  private final String description;
  private final List<MenuEntry> entries;
  private final Consumer<MenuEntry> choiceHandler;
  private int selected = 0;
  Predicate<ButtonClickEvent> accessControlPredicate = e->true;
  
  private ChoiceMenu(String title, String description, List<MenuEntry> entries, Consumer<MenuEntry> handler) {
    super();
    this.entries=entries;
    this.choiceHandler = handler;
    this.description = description;
    this.title = title;
  }

  public static class MenuEntry{
    private final String display;
    private final String value;
    public MenuEntry(String display, String value) {
      super();
      this.display=display;
      this.value=value;
    }
    public String getDisplay() {
      return display;
    }
    public String getValue() {
      return value;
    }
  }
  
  public void display(MessageChannel channel) {
    Message message = channel.sendMessageEmbeds(buildEmbed().build())
        .setActionRow(
            Button.secondary("choiceMenuUp", Emoji.ARROW_UP_SMALL.asEmoji()),
            Button.secondary("choiceMenuDown", Emoji.ARROW_DOWN_SMALL.asEmoji()),
            Button.primary("choiceMenuOkay", Emoji.WHITE_CHECK_MARK.asEmoji()),
            Button.danger("choiceMenuCancel", Emoji.NEGATIVE_SQUARED_CROSS_MARK.asEmoji())
            )
        .complete();
    GlobalMenuListener.register(message.getId(), this);
  }

  private EmbedBuilder buildEmbed() {
    EmbedBuilder builder = new EmbedBuilder();
    builder.addField("Choices",IntStream.range(0,entries.size()).mapToObj(i->((i==selected)?Emoji.ARROW_RIGHT.asString():Emoji.BLACK_LARGE_SQUARE.asString())+" "+entries.get(i).getDisplay()).collect(Collectors.joining("\n")),false);
    builder.addField("Selected option", entries.get(selected).getDisplay(),false);
    builder.setTitle(title);
    builder.setDescription(description);
    return builder;
  }

  public void update(ButtonClickEvent event) {
    event.editMessageEmbeds(buildEmbed().build()).queue();
  }

  public void setAccessControl(Predicate<ButtonClickEvent> pred) {
    this.accessControlPredicate=pred;
  }

  public boolean handleButtonClick(ButtonClickEvent event) {
    if(!accessControlPredicate.test(event)) {
      event.reply("You are not allowed to do that.").setEphemeral(true).queue();
      return false;
    }
    switch(event.getComponentId()) {
    case "choiceMenuUp":
      if(selected == 0) {
        selected = entries.size()-1;
      } else {
        selected--;
      }
      update(event);
      return false;
    case "choiceMenuDown":
      if(selected == entries.size()-1) {
        selected = 0;
      } else {
        selected++;
      }
      update(event);
      return false;
    case "choiceMenuOkay":
      choiceHandler.accept(entries.get(selected));
      event.deferEdit();
      event.getMessage().delete().queue();
      return true;
    case "choiceMenuCancel":
      event.deferEdit();
      event.getMessage().delete().queue();
      return true;
    default:
      return false;
    }
  }

  public static ChoiceMenuBuilder builder() {
    return new ChoiceMenuBuilder();
  }
  
  public static class ChoiceMenuBuilder{
    List<MenuEntry> entries = new ArrayList<>();
    String title = "";
    String description = "";
    Consumer<MenuEntry> choiceHandler;
    
    private ChoiceMenuBuilder() {
      //nothing  
    }
    
    public ChoiceMenuBuilder addEntry(MenuEntry entry) {
      entries.add(entry);
      return this;
    }
    
    public ChoiceMenuBuilder setChoiceHandler(Consumer<MenuEntry> handler) {
      this.choiceHandler = handler;
      return this;
    }
    
    public ChoiceMenuBuilder setTitle(String title) {
      this.title=title;
      return this;
    }
    
    public ChoiceMenuBuilder setDescription(String description) {
      this.description=description;
      return this;
    }
    
    public ChoiceMenu build() {
      if(choiceHandler==null) {
        throw new IllegalStateException("Uninitialized handler for menu");
      }
      return new ChoiceMenu(title,description,entries,choiceHandler);
    }
    
  }

}


// end of file
