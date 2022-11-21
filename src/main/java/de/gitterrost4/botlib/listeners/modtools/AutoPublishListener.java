package de.gitterrost4.botlib.listeners.modtools;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import de.gitterrost4.botlib.listeners.AbstractListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AutoPublishListener extends AbstractListener<ServerConfig> {

  public AutoPublishListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getAutoPublishConfig());
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event) {
    if(config.getAutoPublishConfig().getChannelIds().contains(event.getChannel().getId())) {
      event.getMessage().crosspost().queue();
    }
  }

}
