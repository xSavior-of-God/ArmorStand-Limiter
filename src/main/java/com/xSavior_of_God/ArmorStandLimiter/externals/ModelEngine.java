package com.xSavior_of_God.ArmorStandLimiter.externals;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.xSavior_of_God.ArmorStandLimiter.api.events.onArmorStandRemoveEvent;

public class ModelEngine implements Listener {
    private boolean Enabled = false;

    public ModelEngine() {
        Enabled = true;
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aModelEngine support enabled."));
    }

    @EventHandler
    public void onArmorStandRemove(onArmorStandRemoveEvent event) {
        if (Enabled && ModelEngineAPI.isModeledEntity(event.getEntity().getUniqueId()))
            event.setCancelled(true);
    }
}
