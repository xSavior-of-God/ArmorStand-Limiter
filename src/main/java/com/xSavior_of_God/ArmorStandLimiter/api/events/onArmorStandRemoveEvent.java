package com.xSavior_of_God.ArmorStandLimiter.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * on ArmorStand Remove Event
 * 
 * @author Silviu
 * @version 1.3
 * @since 2021-01-31
 */
public class onArmorStandRemoveEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  private boolean isCancelled = false;
  private Entity armorstand;
  
  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
  
  /**
   * Constructor
   */
  public onArmorStandRemoveEvent(Entity armorstand) {
    this.armorstand = armorstand;
  }
  
  /**
   * 
   * @return ArmorStand Entity
   */
  public Entity getEntity() {
    return armorstand;
  }
  
  /**
   * 
   * @return isCancelled
   */
  public boolean isCancelled() {
    return this.isCancelled;
  }

  /**
   *
   * @param isCancelled
   */
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }
}
