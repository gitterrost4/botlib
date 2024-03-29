// $Id $
// (C) cantamen/Paul Kramer 2020
package de.gitterrost4.botlib.listeners.modtools;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import de.gitterrost4.botlib.containers.ChoiceMenu;
import de.gitterrost4.botlib.containers.ChoiceMenu.ChoiceMenuBuilder;
import de.gitterrost4.botlib.containers.CommandMessage;
import de.gitterrost4.botlib.listeners.AbstractMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * TODO documentation
 */
public class UnmuteListener extends AbstractMessageListener<ServerConfig> {

  public UnmuteListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getMuteConfig(), "unmute");
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event, CommandMessage messageContent) {
    event.getMessage().delete().queue();
    if (!hasAccess(guild().getMember(event.getAuthor()))) {
      event.getChannel().sendMessage("***You don't have the right to unmute people!***").queue();
      return;
    }
    if (!messageContent.getArg(0).isPresent()) {
      event.getChannel().sendMessage("***No user given!***").queue();
      return;
    }
    Set<Member> possibleMembers = new HashSet<>();
    possibleMembers.addAll(event.getMessage().getMentionedMembers());
    possibleMembers.addAll(guild().getMembers().stream()
        .filter(m -> m.getEffectiveName().toLowerCase().contains(messageContent.getArg(0).get().toLowerCase())
            || m.getUser().getAsTag().contains(messageContent.getArg(0).get()))
        .filter(m -> m.getRoles().stream().anyMatch(r -> r.getId().equals(config.getMuteConfig().getMuteRoleId())))
        .collect(Collectors.toSet()));

    if (possibleMembers.size() == 0) {
      event.getChannel().sendMessage("***No muted user found for input " + messageContent.getArg(0).get() + "***")
          .queue();
      return;
    }

    if (possibleMembers.size() > 10) {
      event.getChannel().sendMessage(
          "***Too many muted users found for input " + messageContent.getArg(0).get() + ". Please be more specific.***")
          .queue();
      return;
    }

    ChoiceMenuBuilder builder = ChoiceMenu.builder();

    possibleMembers.stream().map(m -> new ChoiceMenu.MenuEntry(m.getUser().getAsTag(), m.getId()))
        .forEach(builder::addEntry);
    builder.setChoiceHandler(e -> guild().removeRoleFromMember(guild().getMemberById(e.getValue()),
        guild().getRoleById(config.getMuteConfig().getMuteRoleId())).queue(x -> {
          info("Unmuted member {}{}", jda.getUserById(e.getValue()).getAsTag());
          event.getChannel().sendMessage("***Unmuted member " + jda.getUserById(e.getValue()).getAsTag() + "***")
              .queue();
        }));
    builder.setTitle("Unmute member");
    builder.setDescription("Choose a member to be unmuted");

    ChoiceMenu menu = builder.build();
    menu.setAccessControl(e->guild().getMember(e.getUser()).hasPermission(Permission.BAN_MEMBERS));
    menu.display(event.getChannel());
  }

  @Override
  protected boolean hasAccess(Member member) {
    return member.hasPermission(Permission.BAN_MEMBERS);
  }

  @Override
  protected String shortInfoInternal() {
    return "Unmute a member";
  }

  @Override
  protected String usageInternal() {
    return commandString("<SEARCHPHRASE>");
  }

  @Override
  protected String descriptionInternal() {
    return "Unmute a member on the server. You can input a SEARCHPHRASE and (if not too many results are returned) can then interactively choose the member to be unmuted.";
  }

  @Override
  protected String examplesInternal() {
    return commandString("gittertest") + "\n" + "Searches for users matching gittertest and unmutes them.";
  }

}

// end of file
