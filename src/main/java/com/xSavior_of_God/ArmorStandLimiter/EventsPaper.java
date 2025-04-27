package com.xSavior_of_God.ArmorStandLimiter;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class EventsPaper implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityFallEvent(EntityMoveEvent e) {
        if(Main.trackFallEntity.get(e.getEntity().getUniqueId()) != null) {
            final UUID uuid = e.getEntity().getUniqueId();
            double currFallBlocks = Main.trackFallEntity.get(uuid);
            if(e.getTo().getY() != e.getFrom().getY()) {
                double blocks = Math.abs((e.getFrom().getY() - e.getTo().getY()));
                if(Main.EventsDisableArmorStandMovingGravityFallBlocks <= (currFallBlocks + blocks)) {
                    ((ArmorStand) e.getEntity()).setGravity(false);
                    Main.trackFallEntity.remove(uuid);
                } else {
                    Main.trackFallEntity.put(uuid, currFallBlocks + blocks);
                }
            }
        }
    }
}
