package de.gitterrost4.botlib.config.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gitterrost4.botlib.config.containers.modules.AlarmConfig;
import de.gitterrost4.botlib.config.containers.modules.AutoRespondConfig;
import de.gitterrost4.botlib.config.containers.modules.AvatarConfig;
import de.gitterrost4.botlib.config.containers.modules.BanConfig;
import de.gitterrost4.botlib.config.containers.modules.FakeBanConfig;
import de.gitterrost4.botlib.config.containers.modules.HelpConfig;
import de.gitterrost4.botlib.config.containers.modules.MirrorConfig;
import de.gitterrost4.botlib.config.containers.modules.ModlogConfig;
import de.gitterrost4.botlib.config.containers.modules.ModuleConfig;
import de.gitterrost4.botlib.config.containers.modules.ReminderConfig;
import de.gitterrost4.botlib.config.containers.modules.RoleCountConfig;
import de.gitterrost4.botlib.config.containers.modules.SayConfig;
import de.gitterrost4.botlib.config.containers.modules.WhoisConfig;
import de.gitterrost4.botlib.listeners.AlarmListener;
import de.gitterrost4.botlib.listeners.AutoRespondListener;
import de.gitterrost4.botlib.listeners.AvatarListener;
import de.gitterrost4.botlib.listeners.HelpListener;
import de.gitterrost4.botlib.listeners.ListenerManager;
import de.gitterrost4.botlib.listeners.MirrorListener;
import de.gitterrost4.botlib.listeners.ModlogListener;
import de.gitterrost4.botlib.listeners.ReminderListener;
import de.gitterrost4.botlib.listeners.RoleCountListener;
import de.gitterrost4.botlib.listeners.SayListener;
import de.gitterrost4.botlib.listeners.WhoisListener;
import de.gitterrost4.botlib.listeners.modtools.BanListener;
import de.gitterrost4.botlib.listeners.modtools.FakeBanListener;
import de.gitterrost4.botlib.listeners.modtools.UnbanListener;
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
  private AutoRespondConfig autoRespondConfig;
  private AvatarConfig avatarConfig;
  private MirrorConfig mirrorConfig;
  private ModlogConfig modlogConfig;
  private ReminderConfig reminderConfig;
  private RoleCountConfig roleCountConfig;
  private SayConfig sayConfig;
  private WhoisConfig whoisConfig;
  private BanConfig banConfig;
  private FakeBanConfig fakeBanConfig;
  
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

  public AutoRespondConfig getAutoRespondConfig() {
    return autoRespondConfig;
  }

  public AvatarConfig getAvatarConfig() {
    return avatarConfig;
  }
  
  public MirrorConfig getMirrorConfig() {
    return mirrorConfig;
  }

  public ModlogConfig getModlogConfig() {
    return modlogConfig;
  }

  public ReminderConfig getReminderConfig() {
    return reminderConfig;
  }

  public RoleCountConfig getRoleCountConfig() {
    return roleCountConfig;
  }

  public SayConfig getSayConfig() {
    return sayConfig;
  }

  public WhoisConfig getWhoisConfig() {
    return whoisConfig;
  }

  public BanConfig getBanConfig() {
    return banConfig;
  }

  public FakeBanConfig getFakeBanConfig() {
    return fakeBanConfig;
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
    if (Optional.ofNullable(getAutoRespondConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new AutoRespondListener(jda, guild, this));
    }
    if (Optional.ofNullable(getAvatarConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new AvatarListener(jda, guild, this));
    }
    if (Optional.ofNullable(getMirrorConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new MirrorListener(jda, guild, this));
    }
    if (Optional.ofNullable(getModlogConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new ModlogListener(jda, guild, this));
    }
    if (Optional.ofNullable(getReminderConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new ReminderListener(jda, guild, this));
    }
    if (Optional.ofNullable(getRoleCountConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new RoleCountListener(jda, guild, this));
    }
    if (Optional.ofNullable(getSayConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new SayListener(jda, guild, this));
    }
    if (Optional.ofNullable(getWhoisConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new WhoisListener(jda, guild, this));
    }
    if (Optional.ofNullable(getBanConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new BanListener(jda, guild, this));
      manager.addEventListener(new UnbanListener(jda, guild, this));
    }
    if (Optional.ofNullable(getFakeBanConfig()).map(ModuleConfig::isEnabled).orElse(false)) {
      manager.addEventListener(new FakeBanListener(jda, guild, this));
    }
    addServerModules(jda, guild, manager);
  }

  protected abstract void addServerModules(JDA jda, Guild guild, ListenerManager manager);

}
