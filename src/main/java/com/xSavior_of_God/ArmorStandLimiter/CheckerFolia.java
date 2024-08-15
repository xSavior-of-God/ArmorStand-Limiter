package com.xSavior_of_God.ArmorStandLimiter;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckerFolia {
    boolean isChunkRunning = false;
    int chunkRegionTaskCounter = 0;
    boolean isBlockRunning = false;
    int blockRegionTaskCounter = 0;

    public CheckerFolia() {
        if (Main.armorStandLimitBlockTaskEnabled) taskBlock();
        if (Main.armorStandLimitChunkTaskEnabled) taskChunk();
    }

    private void taskBlock() {
        Main.scheduler.runTaskTimerAsynchronously(Main.instance, () -> {
            if (isBlockRunning || blockRegionTaskCounter > 0) return;
            isBlockRunning = true;

            for (final World w : Bukkit.getServer().getWorlds()) {
                if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;

                if (w.getLoadedChunks().length < 1) continue;

                for (final Chunk c : w.getLoadedChunks()) {
                    blockRegionTaskCounter++;
                    Main.scheduler.runAsRegionScheduler(Main.instance, c, () -> {
                        try {
                            if (c.getEntities() == null || c.getEntities().length < 1) {
                                blockRegionTaskCounter--;
                                return;
                            }
                            Map<Location, List<Entity>> counterBlock = new HashMap<Location, List<Entity>>();

                            for (final Entity e : c.getEntities()) {
                                if (e instanceof ArmorStand && Utils.checkArmorStand((ArmorStand) e)) {
                                    Location loc = e.getLocation();

                                    loc.setY(0);
                                    int x = (int) loc.getX();
                                    int z = (int) loc.getZ();
                                    loc.setX(x);
                                    loc.setZ(z);
                                    loc.setPitch(0);
                                    loc.setYaw(0);
                                    Vector vec = new Vector();
                                    loc.setDirection(vec);

                                    if (counterBlock.containsKey(loc)) {
                                        counterBlock.get(loc).add(e);
                                    } else {
                                        List<Entity> eList = new ArrayList<Entity>();
                                        eList.add(e);
                                        counterBlock.put(loc, eList);
                                    }
                                }
                            }

                            counterBlock.forEach((location, counter) -> {
                                if (counter.size() >= Main.armorStandLimitBlockTrigger) {
                                    Notifications.send(location, counter.size());

                                    for (Entity e : counter) {
                                        try {
                                            ArmorStand arm = (ArmorStand) e;
                                            arm.remove();
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            });
                            counterBlock.clear();
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        blockRegionTaskCounter--;
                    }, false);
                }
            }
            isBlockRunning = false;

        }, Main.armorStandLimitBlockTaskRefresh * 20L, Main.armorStandLimitBlockTaskRefresh * 20L);
    }

    private void taskChunk() {
        Main.scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {

            @Override
            public void run() {
                if (isChunkRunning || chunkRegionTaskCounter > 0) return;
                isChunkRunning = true;

                for (final World w : Bukkit.getServer().getWorlds()) {
                    if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;
                    if (w.getLoadedChunks().length < 1) {
                        continue;
                    }

                    for (final Chunk c : w.getLoadedChunks()) {
                        chunkRegionTaskCounter++;
                        Main.scheduler.runAsRegionScheduler(Main.instance, c, () -> {
                            Map<Chunk, List<Entity>> counterChunk = new HashMap<Chunk, List<Entity>>();
                            try {
                                if (c.getEntities() == null || c.getEntities().length < 1) {
                                    chunkRegionTaskCounter--;
                                    return;
                                }
                                for (final Entity e : c.getEntities()) {
                                    if (e instanceof ArmorStand && Utils.checkArmorStand((ArmorStand) e)) {
                                        if (counterChunk.containsKey(c)) {
                                            counterChunk.get(c).add(e);
                                        } else {
                                            List<Entity> eList = new ArrayList<Entity>();
                                            eList.add(e);
                                            counterChunk.put(c, eList);
                                        }
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }

                            counterChunk.forEach((chunk, counter) -> {
                                if (counter.size() >= Main.armorStandLimitChunkTrigger) {
                                    Notifications.send(chunk, counter.size());
                                    for (Entity e : counter) {
                                        try {
                                            ArmorStand arm = (ArmorStand) e;
                                            arm.remove();
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            });

                            counterChunk.clear();
                            chunkRegionTaskCounter--;
                        }, false);
                    }
                }
                isChunkRunning = false;
            }

        }, Main.armorStandLimitChunkTaskRefresh * 20L, Main.armorStandLimitChunkTaskRefresh * 20L);
    }

}
