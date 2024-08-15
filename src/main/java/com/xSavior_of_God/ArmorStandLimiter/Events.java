package com.xSavior_of_God.ArmorStandLimiter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Events implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlaceArmorStand(PlayerInteractEvent e) {
        if (!Main.LimitArmorStandPlaceForChunk || e.isCancelled())
            return;
        if (Main.ChecksDisabledWorlds.contains(e.getPlayer().getWorld().getName()))
            return;
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("armorstandlimiter.bypass"))
            return;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDispenseEvent(BlockDispenseEvent e) {
        if (!Main.DisableDispenserSpawningArmorStand || e.isCancelled())
            return;
        if (Main.ChecksDisabledWorlds.contains(e.getBlock().getWorld().getName()))
            return;
        if (e.getItem().getType().equals(Material.ARMOR_STAND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawnEvent(EntitySpawnEvent e) {
        if (!Main.EventsDisableArmorStandMovingGravityEnabled || e.isCancelled())
            return;
        if (Main.ChecksDisabledWorlds.contains(e.getEntity().getWorld().getName()))
            return;

        Entity entity = e.getEntity();
        if (entity instanceof ArmorStand) {
            int armorStandCounter = 0;
            for (final Entity ent : entity.getLocation().getChunk().getEntities()) {
                if(armorStandCounter > Main.EventsDisableArmorStandMovingGravityRequired)
                    break;
                if (ent instanceof ArmorStand && Utils.checkArmorStand((ArmorStand) ent)) {
                    armorStandCounter++;
                }
            }
            if (armorStandCounter > Main.EventsDisableArmorStandMovingGravityRequired) {
                ((ArmorStand) entity).setGravity(false);
            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent e) {
        if (!Main.EventsDisableArmorStandMovingPistonEnabled || e.isCancelled())
            return;
        if (Main.ChecksDisabledWorlds.contains(e.getBlock().getWorld().getName()))
            return;


        Location loc = e.getBlock().getLocation();
        Location locToCheck = loc;


        if (e.getBlocks().isEmpty()) {
            switch (e.getDirection()) {
                case UP:
                    locToCheck.add(0, 1, 0);
                    break;
                case DOWN:
                    locToCheck.add(0, -1, 0);
                    break;
                case NORTH:
                    locToCheck.add(0, 0, -1);
                    break;
                case SOUTH:
                    locToCheck.add(0, 0, 1);
                    break;
                case EAST:
                    locToCheck.add(1, 0, 0);
                    break;
                case WEST:
                    locToCheck.add(-1, 0, 0);
                    break;
                default:
                    Bukkit.getConsoleSender()
                            .sendMessage("[ArmorStandEvents] ERROR #B#! |  " + e.getBlock().getLocation().toString());
                    break;
            }
            for (final Entity entity : locToCheck.getChunk().getEntities()) {
                if (entity instanceof ArmorStand) {
                    ArmorStand arm = (ArmorStand) entity;
                    if (!Utils.checkArmorStand(arm)) continue;
                    final int X = entity.getLocation().getBlockX();
                    final int Y = entity.getLocation().getBlockY();
                    final int Z = entity.getLocation().getBlockZ();
                    if (((int) locToCheck.getX()) == X && (((int) locToCheck.getY()) == Y || ((int) locToCheck.getY() - 1) == Y)
                            && ((int) locToCheck.getZ()) == Z) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        } else {
            List<Chunk> chunks = new ArrayList<Chunk>();
            for (Block block : e.getBlocks()) {
                Location blockLocation = block.getLocation();
                switch (e.getDirection()) {
                    case UP:
                        blockLocation.add(0, 1, 0);
                        break;
                    case DOWN:
                        blockLocation.add(0, -1, 0);
                        break;
                    case NORTH:
                        blockLocation.add(0, 0, -1);
                        break;
                    case SOUTH:
                        blockLocation.add(0, 0, 1);
                        break;
                    case EAST:
                        blockLocation.add(1, 0, 0);
                        break;
                    case WEST:
                        blockLocation.add(-1, 0, 0);
                        break;
                    default:
                        Bukkit.getConsoleSender()
                                .sendMessage("[ArmorStandEvents] ERROR #B#! |  " + e.getBlock().getLocation().toString());
                        break;
                }
                if (!chunks.contains(blockLocation.getChunk()))
                    chunks.add(blockLocation.getChunk());
            }
            for (Chunk chunk : chunks) {
                int armorStandCounter = 0;
                for (final Entity ent : chunk.getEntities()) {
                    if(armorStandCounter > Main.EventsDisableArmorStandMovingPistonRequired)
                        break;
                    if (ent instanceof ArmorStand && Utils.checkArmorStand((ArmorStand) ent))
                            armorStandCounter++;
                }
                if (armorStandCounter > Main.EventsDisableArmorStandMovingPistonRequired) {
                    e.setCancelled(true);
                    return;
                }
            }

        }
    }
}
