package com.xSavior_of_God.ArmorStandLimiter.externals;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.xSavior_of_God.ArmorStandLimiter.Main;
import com.xSavior_of_God.ArmorStandLimiter.api.events.onArmorStandRemoveEvent;

public class ModelEngine implements Listener {
    private boolean Enabled = false;

    public ModelEngine() {
        Enabled = true;
    }

    @EventHandler
    public void onArmorStandRemove(onArmorStandRemoveEvent event) {
        if (Enabled && Main.ChecksDisableIfIsModelEngineEntity && ModelEngineAPI.api.getModelManager().getModeledEntity(event.getEntity().getUniqueId()) != null)
            event.setCancelled(true);
    }

}
