package de.gitterrost4.botlib.listeners;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotTrapListener extends AbstractListener<ServerConfig>{

  public BotTrapListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getBotTrapConfig());
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event) {
    super.messageReceived(event);
    if(event.getChannel().getId().equals(config.getBotTrapConfig().getChannelId())) {
      event.getMember().ban(7, "Spamming. Message: \n"+event.getMessage().getContentStripped()).queue();
    }
  }

}
