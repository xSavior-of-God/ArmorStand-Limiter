package com.xSavior_of_God.ArmorStandLimiter.externals;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.xSavior_of_God.ArmorStandLimiter.api.events.onArmorStandRemoveEvent;

public class HolographicDisplays implements Listener {
    private boolean Enabled = false;

    public HolographicDisplays() {
        Enabled = true;
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aHolographicDisplays support enabled."));
    }

    @EventHandler
    public void onArmorStandRemove(onArmorStandRemoveEvent event) {
        if (Enabled && HologramsAPI.isHologramEntity(event.getEntity()))
            event.setCancelled(true);
    }
}
