package de.gitterrost4.botlib.listeners;

import java.util.ArrayList;
import java.util.List;

import de.gitterrost4.botlib.config.containers.ServerConfig;
import net.dv8tion.jda.api.JDA;

public class ListenerManager {
  public ListenerManager(JDA jda) {
    super();
    this.jda = jda;
  }

  private JDA jda;
  
  private List<AbstractListener<? extends ServerConfig>> listeners = new ArrayList<>();

  public List<AbstractListener<? extends ServerConfig>> getListeners() {
    return listeners;
  }
  
  public void addEventListener(AbstractListener<? extends ServerConfig> listener) {
    listeners.add(listener);
    jda.addEventListener(listener);
  }
}
