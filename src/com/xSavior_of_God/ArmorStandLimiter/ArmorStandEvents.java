package com.xSavior_of_God.ArmorStandLimiter;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;

public class ArmorStandEvents implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlaceArmorStand(PlayerInteractEvent e) {
		if (!Main.LimitArmorStandPlaceForChunk || e.isCancelled()) {
			return;
		}

		int armorStandCounter = 1;
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& e.getPlayer().getItemInHand().getType().equals(Material.ARMOR_STAND)) {
			for (final Entity entity : e.getPlayer().getLocation().getChunk().getEntities()) {
				if (entity instanceof ArmorStand) {
					armorStandCounter++;
				}
			}
			if (armorStandCounter > Main.armorStandLimitChunkTrigger) {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
						Main.tooManyArmorStand.replace("{max}", Main.armorStandLimitChunkTrigger + "")));
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDispenserPlaceEvent(BlockDispenseEvent e) {
		if (!Main.DisableDispenserSpawningArmorstand || e.isCancelled()) {
			return;
		}
		if( e.getItem().getType().equals(Material.ARMOR_STAND)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics(EntitySpawnEvent e) {
		if (!Main.Water || e.isCancelled()) {
			return;
		}
		Entity entity = e.getEntity();
		if (entity instanceof ArmorStand) {
			((ArmorStand) entity).setMarker(false);
			((ArmorStand) entity).setGravity(false);
		}
	}
}
