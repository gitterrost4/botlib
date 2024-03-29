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
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * TODO documentation
 */
public class UnbanListener extends AbstractMessageListener<ServerConfig> {

  public UnbanListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getBanConfig(), "unban");
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event, CommandMessage messageContent) {
    event.getMessage().delete().queue();
    if (!hasAccess(guild().getMember(event.getAuthor()))) {
      event.getChannel().sendMessage("***You don't have the right to unban people!***").queue();
      return;
    }
    if (!messageContent.getArg(0).isPresent()) {
      event.getChannel().sendMessage("***No user given!***").queue();
      return;
    }
    Set<User> possibleMembers = new HashSet<>();
    guild().retrieveBanList()
        .queue(banList -> possibleMembers.addAll(banList.stream().map(Ban::getUser)
            .filter(u -> u.getName().toLowerCase().contains(messageContent.getArg(0).get().toLowerCase())
                || u.getAsTag().contains(messageContent.getArg(0).get()))
            .collect(Collectors.toSet())));

    if (possibleMembers.size() == 0) {
      event.getChannel().sendMessage("***No banned user found for input " + messageContent.getArg(0).get() + "***")
          .queue();
      return;
    }

    if (possibleMembers.size() > 10) {
      event.getChannel().sendMessage("***Too many banned users found for input " + messageContent.getArg(0).get()
          + ". Please be more specific.***").queue();
      return;
    }

    ChoiceMenuBuilder builder = ChoiceMenu.builder();

    possibleMembers.stream().map(u -> new ChoiceMenu.MenuEntry(u.getAsTag(), u.getId())).forEach(builder::addEntry);
    builder.setChoiceHandler(e -> guild().unban(e.getValue()).queue(x -> {
      info("Unbanned user {}{}", jda.getUserById(e.getValue()).getAsTag());
      event.getChannel().sendMessage("***Unbanned user " + jda.getUserById(e.getValue()).getAsTag() + "***").queue();
    }));
    builder.setTitle("Unban user");
    builder.setDescription("Choose a user to be unbanned");

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
    return "Unban a member";
  }

  @Override
  protected String usageInternal() {
    return commandString("<SEARCHPHRASE>");
  }

  @Override
  protected String descriptionInternal() {
    return "Unban a member from the server. You can input a SEARCHPHRASE and (if not too many results are returned) can then interactively choose the member to be unbanned.";
  }

  @Override
  protected String examplesInternal() {
    return commandString("gittertest") + "\n" + "Searches for users matching gittertest and unbans them.";
  }

}

// end of file
