package de.gitterrost4.botlib.config.containers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;

public abstract class MainConfig<T extends ServerConfig> {
  private List<T> servers;

  private static final Logger logger = LoggerFactory.getLogger(MainConfig.class);

  public List<T> getServers() {
    return servers;
  }

  public abstract T getDefaultConfig();
  
  public void addDefaultServerConfigIfAbsent(String serverId, String serverName, JDA jda, Runnable saveAction) {
    if (!servers.stream().anyMatch(serverConfig -> serverConfig.getServerId().equals(serverId))) {
      // we don't have a config for this server. We need this check because when
      // discord is down, this event may be triggered for servers we already joined.
      logger.info("Adding Guild {}({})", serverId, serverName);
      T config = getDefaultConfig();
      config.serverId = serverId;
      config.name = serverName;
      config.databaseFileName = serverId + ".db";
      servers.add(config);
      config.iAddServerModules(jda);
      saveAction.run();
    }
  }

}
