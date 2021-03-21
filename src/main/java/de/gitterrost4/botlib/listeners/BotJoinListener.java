package de.gitterrost4.botlib.listeners;

import java.util.function.Supplier;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotJoinListener<S extends ServerConfig, T extends de.gitterrost4.botlib.config.containers.MainConfig<S>> extends ListenerAdapter {

  private final JDA jda;
  private Supplier<T> configSupplier;
  private Runnable saveConfig;

  public BotJoinListener(JDA jda, Supplier<T> configSupplier, Runnable saveConfig) {
    super();
    this.jda = jda;
    this.configSupplier = configSupplier;
    this.saveConfig = saveConfig;
  }

  @Override
  public void onGuildJoin(GuildJoinEvent event) {
    super.onGuildJoin(event);
    Guild guild = event.getGuild();
    T config = configSupplier.get();
    config.addDefaultServerConfigIfAbsent(guild.getId(), guild.getName(), jda, ()->saveConfig.run());
  }

}
