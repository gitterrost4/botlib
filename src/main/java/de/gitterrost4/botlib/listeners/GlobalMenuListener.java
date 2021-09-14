package de.gitterrost4.botlib.listeners;

import java.util.HashMap;
import java.util.Map;

import de.gitterrost4.botlib.containers.ChoiceMenu;
import de.gitterrost4.botlib.containers.PagedEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GlobalMenuListener extends ListenerAdapter{
  private static GlobalMenuListener INSTANCE = new GlobalMenuListener();
  public static GlobalMenuListener instance() {return INSTANCE;}
  
  public Map<String, ChoiceMenu> activeMenus = new HashMap<>();
  public Map<String, PagedEmbed> activePagedEmbeds = new HashMap<>();
  
  @Override
  public void onButtonClick(ButtonClickEvent event) {
    if (event.getUser().isBot()) {
      return;
    }
    if (activePagedEmbeds.containsKey(event.getMessageId())) {
      activePagedEmbeds.get(event.getMessageId()).handleButtonClick(event);
    }
    if (activeMenus.containsKey(event.getMessageId())) {
      activeMenus.get(event.getMessageId()).handleButtonClick(event);
    }
  }
  
  private void doRegister(String messageId, ChoiceMenu menu) {
    activeMenus.put(messageId, menu);
  }

  public static void register(String messageId, ChoiceMenu menu) {
    instance().doRegister(messageId, menu);
  }

  private void doRegister(String messageId, PagedEmbed menu) {
    activePagedEmbeds.put(messageId, menu);
  }
  
  public static void register(String messageId, PagedEmbed menu) {
    instance().doRegister(messageId, menu);
  }

}
