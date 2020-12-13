package com.xSavior_of_God.ArmorStandLimiter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityEvent;
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
					((ArmorStand) entity).setMarker(false);
					((ArmorStand) entity).setGravity(false);
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
		if (e.getItem().getType().equals(Material.ARMOR_STAND)) {
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPistonPush(BlockPistonExtendEvent e) {
		Location loc = e.getBlock().getLocation();
		//System.out.println("PRE LOC: " + loc.getBlockX() + " LOC: " + loc.getBlockY() + " LOC: " + loc.getBlockZ());
		//loc.add(e.getDirection().getDirection().getBlockX(),e.getDirection().getDirection().getBlockY(),e.getDirection().getDirection().getBlockZ());
		System.out.println("LOC: " + loc.getBlockX() + " LOC: " + loc.getBlockY() + " LOC: " + loc.getBlockZ());
		if (loc.getBlock().getType().equals(Material.ARMOR_STAND)) {
			e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPistonPush(EntityChangeBlockEvent e) {
		Entity entity = e.getEntity();
		System.out.println(entity.getLocation());
		if (entity instanceof ArmorStand) {
			entity.remove();
		}
	}
}
