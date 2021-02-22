package de.gitterrost4.botlib.config.containers.modules;

import java.util.List;

public abstract class CommandModuleConfig extends ModuleConfig {
  private List<String> restrictToChannels;

  public List<String> getRestrictToChannels() {
    return restrictToChannels;
  }
  
}
