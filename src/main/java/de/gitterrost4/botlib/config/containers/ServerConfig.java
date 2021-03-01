package de.gitterrost4.botlib.config.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gitterrost4.botlib.config.containers.modules.AlarmConfig;
import de.gitterrost4.botlib.config.containers.modules.HelpConfig;
import de.gitterrost4.botlib.config.containers.modules.ModuleConfig;
import de.gitterrost4.botlib.listeners.AlarmListener;
import de.gitterrost4.botlib.listeners.HelpListener;
import de.gitterrost4.botlib.listeners.ListenerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public abstract class ServerConfig {
  private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

  String name;
  String serverId;
  private List<String> botPrefixes;
  private List<String> superUserRoles = new ArrayList<>();
  String databaseFileName;
  private HelpConfig helpConfig;
  private AlarmConfig alarmConfig;

  
  
  @Override
  public String toString() {
    return "ServerConfig [name=" + name + ", serverId=" + serverId + ", botPrefixes=" + botPrefixes
        + ", superUserRoles=" + superUserRoles + ", databaseFileName=" + databaseFileName + ", helpConfig=" + helpConfig
        + ", alarmConfig=" + alarmConfig + "]";
  }

  public List<String> getSuperUserRoles() {
    return superUserRoles;
  }

  public String getName() {
    return name;
  }

  public String getServerId() {
    return serverId;
  }

  public List<String> getBotPrefixes() {
    return botPrefixes;
  }

  public String getDatabaseFileName() {
    return databaseFileName;
  }

  public HelpConfig getHelpConfig() {
    return helpConfig;
  }
  
  public AlarmConfig getAlarmConfig() {
    return alarmConfig;
  }

  public void iAddServerModules(JDA jda) {
    Guild guild = jda.getGuildById(getServerId());
    if (guild == null) {
      logger.warn("Guild {}({}) does not exist anymore", getServerId(), getName());
      return;
    }
    logger.info("Adding Guild {}({})", guild.getId(), guild.getName());
    ListenerManager manager = new ListenerManager(jda);
    if (Optional.ofNullable(getHelpConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new HelpListener(jda, guild, this, manager));
    }
    if (Optional.ofNullable(getAlarmConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new AlarmListener(jda, guild, this));
    }
    addServerModules(jda, guild, manager);
  }

  protected abstract void addServerModules(JDA jda, Guild guild, ListenerManager manager);

}
