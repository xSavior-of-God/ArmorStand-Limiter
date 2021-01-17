package com.xSavior_of_God.ArmorStandLimiter;

import org.bukkit.entity.Entity;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class AddonHolographicDisplays {
  
  public static boolean checkEntity(Entity e) {
    return HologramsAPI.isHologramEntity(e);
  }
  
  
}
