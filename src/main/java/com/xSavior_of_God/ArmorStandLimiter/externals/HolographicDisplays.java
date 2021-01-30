package com.xSavior_of_God.ArmorStandLimiter.externals;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.xSavior_of_God.ArmorStandLimiter.api.events.onArmorStandRemove;

public class HolographicDisplays implements Listener {
  private boolean Enabled = false;

  public HolographicDisplays() {
    Enabled = true;
  }

  @EventHandler
  public void onArmorStandRemove(onArmorStandRemove event) {
    if (Enabled && HologramsAPI.isHologramEntity(event.getEntity()))
      event.setCancelled(true);
  }
}
