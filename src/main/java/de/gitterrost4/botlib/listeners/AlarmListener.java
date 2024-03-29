package de.gitterrost4.botlib.listeners;

import java.awt.Color;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import de.gitterrost4.botlib.containers.CommandMessage;
import de.gitterrost4.botlib.containers.PagedEmbed;
import de.gitterrost4.botlib.database.ConnectionHelper;
import de.gitterrost4.botlib.helpers.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AlarmListener extends AbstractMessageListener<ServerConfig> {

  public AlarmListener(JDA jda, Guild guild, ServerConfig config) {
    super(jda, guild, config, config.getAlarmConfig(), "alarm");
    connectionHelper.update(
        "create table if not exists alarm (id INTEGER PRIMARY KEY not null, name text not null UNIQUE, cronexpression text not null, channel text not null, ping text not null, title text not null, message text not null, durationseconds integer, lastrun text null, active integer default 1);");
    Timer t = new Timer();
    t.scheduleAtFixedRate(
        Utilities.timerTask(
            () -> Alarm.readAlarmsFromDatabase(connectionHelper, guild(), true).stream().forEach(alarm -> alarm.run())),
        10000, 10000);
  }

  @Override
  protected void messageReceived(MessageReceivedEvent event, CommandMessage message) {
    switch (message.getTokenizedArgOrThrow(0)) {
    case "add":
      String newName = message.getTokenizedArgOrThrow(1);
      String cronExpression = message.getTokenizedArgOrThrow(2);
      Integer durationSeconds = Integer.valueOf(message.getTokenizedArgOrThrow(3));
      String channel = guild.getTextChannels().stream()
          .filter(c -> message.getTokenizedArgOrThrow(4).replace("!", "").equals(c.getAsMention().replace("!", "")))
          .findFirst().map(TextChannel::getId).orElseThrow(() -> new IllegalArgumentException("channel not found"));
      String ping = message.getTokenizedArgOrThrow(5);
      String title = message.getTokenizedArgOrThrow(6);
      String newMessage = message.getTokenizedArgOrThrow(7);
      Alarm newAlarm = new Alarm(newName, cronExpression, channel, ping, title, newMessage, durationSeconds, Instant.now().atZone(ZoneId.of("Z")),
          connectionHelper, guild);
      newAlarm.writeToDatabase();
      event.getChannel().sendMessageEmbeds(newAlarm.toEmbed()).queue();
      break;
    case "activate":
      String aName = message.getTokenizedArgOrThrow(1);
      Alarm.findByName(aName, connectionHelper, guild()).ifPresent(alarm -> {
        alarm.activate();
        event.getChannel().sendMessageEmbeds(alarm.toEmbed()).queue();
      });

      break;
    case "deactivate":
      String dName = message.getTokenizedArgOrThrow(1);
      Alarm.findByName(dName, connectionHelper, guild()).ifPresent(alarm -> {
        alarm.deactivate();
        event.getChannel().sendMessageEmbeds(alarm.toEmbed()).queue();
      });
      break;
    case "delete":
      String name = message.getTokenizedArgOrThrow(1);
      Alarm.findByName(name, connectionHelper, guild()).ifPresent(alarm -> {
        alarm.delete();
        event.getChannel().sendMessage("Alarm " + name + " has been deleted.").queue();
      });
      break;
    case "list":
      PagedEmbed pagedEmbed = new PagedEmbed(Alarm.readAlarmsFromDatabase(connectionHelper, guild()).stream()
          .map(Alarm::toEmbed).collect(Collectors.toList()));
      pagedEmbed.display(event.getChannel());
      break;
    }
  }

  @Override
  protected String descriptionInternal() {
    // TODO Auto-generated method stub
    return super.descriptionInternal();
  }

  @Override
  protected String examplesInternal() {
    // TODO Auto-generated method stub
    return super.examplesInternal();
  }

  @Override
  protected String helpInternal() {
    // TODO Auto-generated method stub
    return super.helpInternal();
  }

  @Override
  protected String shortInfoInternal() {
    // TODO Auto-generated method stub
    return super.shortInfoInternal();
  }

  @Override
  protected String usageInternal() {
    // TODO Auto-generated method stub
    return super.usageInternal();
  }

  private static class Alarm {
    private Integer id;
    private final String name;
    private final String cronexpression;
    private final String channel;
    private final String ping;
    private final String title;
    private final String message;
    private final Integer durationseconds;
    private ZonedDateTime lastrun;
    private Boolean active;
    private final ConnectionHelper connectionHelper;
    private final Guild guild;

    private Alarm(Integer id, String name, String cronexpression, String channel, String ping, String title,
        String message, Integer durationseconds, ZonedDateTime lastrun, Boolean active,
        ConnectionHelper connectionHelper, Guild guild) {
      super();
      this.id = id;
      this.name = name;
      this.cronexpression = cronexpression;
      this.channel = channel;
      this.ping = ping;
      this.title = title;
      this.message = message;
      this.durationseconds = durationseconds;
      this.lastrun = lastrun;
      this.active = active;
      this.connectionHelper = connectionHelper;
      this.guild = guild;
    }

    private Alarm(String name, String cronexpression, String channel, String ping, String title, String message,
        Integer durationseconds, ZonedDateTime lastrun, ConnectionHelper connectionHelper, Guild guild) {
      this(null, name, cronexpression, channel, ping, title, message, durationseconds, lastrun, true, connectionHelper,
          guild);
    }

    private void writeToDatabase() {
      connectionHelper.update(
          "replace into alarm (name, cronexpression, channel, ping, title, message, durationseconds, lastrun,active) VALUES (?,?,?,?,?,?,?,?,?)",
          name, cronexpression, channel, ping, title, message, durationseconds, lastrun, active);
      this.id = connectionHelper.getFirstResult("select id from alarm where name=?", rs -> rs.getInt("id"), name)
          .orElse(null);
    }

    private static List<Alarm> readAlarmsFromDatabase(ConnectionHelper connectionHelper, Guild guild) {
      return readAlarmsFromDatabase(connectionHelper, guild, false);
    }

    private static List<Alarm> readAlarmsFromDatabase(ConnectionHelper connectionHelper, Guild guild,
        Boolean onlyActive) {
      return connectionHelper.getResults(
          "select id, name, cronexpression, channel, ping, title, message, durationseconds, lastrun, active from alarm"
              + (onlyActive ? " where active>0" : ""),
          rs -> new Alarm(rs.getInt("id"), rs.getString("name"), rs.getString("cronexpression"),
              rs.getString("channel"), rs.getString("ping"), rs.getString("title"), rs.getString("message"),
              rs.getInt("durationseconds"), ZonedDateTime.parse(rs.getString("lastrun")), rs.getBoolean("active"),
              connectionHelper, guild));
    }

    private static Optional<Alarm> findByName(String name, ConnectionHelper connectionHelper, Guild guild) {
      return connectionHelper.getFirstResult(
          "select id, name, cronexpression,channel,ping,title,message,durationseconds,lastrun,active from alarm where name=?",
          rs -> new Alarm(rs.getInt("id"), rs.getString("name"), rs.getString("cronexpression"),
              rs.getString("channel"), rs.getString("ping"), rs.getString("title"), rs.getString("message"),
              rs.getInt("durationseconds"), ZonedDateTime.parse(rs.getString("lastrun")), rs.getBoolean("active"),
              connectionHelper, guild),
          name);
    }

    private void delete() {
      connectionHelper.update("delete from alarm where id=?", id);
    }

    private void activate() {
      this.active = true;
      writeToDatabase();
    }

    private void deactivate() {
      this.active = false;
      writeToDatabase();
    }

    private boolean shouldBeRun() {
      CronParser parser = getCronParser();
      ExecutionTime time = ExecutionTime.forCron(parser.parse(cronexpression));
      return time.nextExecution(lastrun).filter(nextRun -> nextRun.isBefore(Instant.now().atZone(ZoneId.of("Z")))).isPresent();
    }

    private static CronParser getCronParser() {
      return new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    }

    private void run() {
      if (shouldBeRun()) {
        guild.getTextChannelById(channel).sendMessage(ping)
            .setEmbeds(new EmbedBuilder().setTitle(title).setDescription(message).setColor(Color.ORANGE).build())
            .queue(message -> {
              if (durationseconds != null && durationseconds > 0) {
                new Timer().schedule(Utilities.timerTask(() -> message.delete().queue()), durationseconds * 1000);
              }
            });
        lastrun = Instant.now().atZone(ZoneId.of("Z"));
        writeToDatabase();
      }
    }

    private MessageEmbed toEmbed() {
      EmbedBuilder builder = new EmbedBuilder();
      return builder.setAuthor(name)
          .addField("Cron Expression",
              cronexpression + "\n("
                  + CronDescriptor.instance(Locale.US).describe(getCronParser().parse(cronexpression)) + ")",
              true)
          .addField("Next Execution",
              ExecutionTime.forCron(getCronParser().parse(cronexpression)).nextExecution(lastrun)
                  .map(x->x.toInstant().getEpochSecond())
                  .map(x->"<t:"+x+":R>").orElse("unknown"),
              true)
          .addField("Active", active ? "Yes" : "No", true)
          .addField("Duration(seconds)", durationseconds.toString(),true)
          .addField("Channel", guild.getTextChannelById(channel).getAsMention(), false).addField("Ping", ping, false)
          .addField("Title", title, false)
          .addField("Text", message.length() < 100 ? message : message.substring(0, 100) + "...", false).build();
    }
  }

  @Override
  protected boolean hasAccess(Member member) {
    return isSuperUser(member);
  }

}
