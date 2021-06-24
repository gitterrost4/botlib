package de.gitterrost4.botlib.listeners;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import de.gitterrost4.botlib.containers.CommandMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GibListener extends AbstractMessageListener<ServerConfig> {

  public GibListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getGibConfig(), "gib");
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event, CommandMessage messageContent) {
    event.getMessage().delete().queue();
    event.getChannel().sendMessage("༼ つ ◕_◕ ༽つ "+messageContent.getArg(0,true).orElse("")).queue();
  }

  @Override
  protected String shortInfoInternal() {
    return "Display a gib emoji";
  }

  @Override
  protected String usageInternal() {
    return commandString("[MESSAGE]");
  }

  @Override
  protected String descriptionInternal() {
    return "Display the gib emoji along with a message"; 
  }

  @Override
  protected String examplesInternal() {
    return commandString("giftcode!")+"\n"
        + "display the gib emoji along with the message";
  }
  
}
