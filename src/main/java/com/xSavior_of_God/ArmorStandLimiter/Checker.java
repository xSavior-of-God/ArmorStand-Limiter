package com.xSavior_of_God.ArmorStandLimiter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Checker {
    long myTime = System.currentTimeMillis();
    boolean isChunkRunning = false;
    boolean isBlockRunning = false;


    public Checker() {
        if (Main.TPSMeterEnabled) TPSMeterTask();
        if (Main.armorStandLimitBlockTaskEnabled) taskBlock();
        if (Main.armorStandLimitChunkTaskEnabled) taskChunk();
    }

    public void TPSMeterTask() {
        Main.scheduler.runTaskTimer(Main.instance, () -> {
            double TPS = 20.0;

            try {
                Class<?> craftServer = null;
                try {
                    craftServer = Class.forName("org.bukkit.craftbukkit.CraftServer");
                } catch(Exception ignored) {
                    craftServer = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftServer");
                }
                Object getServer = craftServer.getMethod("getServer").invoke(Bukkit.getServer());
                Field tpsField = getServer.getClass().getField("recentTps");
                double[] tps = ((double[]) tpsField.get(getServer));
                TPS = tps[0];
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                     NoSuchMethodException | SecurityException | NoSuchFieldException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            if (TPS < Main.TPSMeterTrigger && myTime < System.currentTimeMillis()) {
                myTime = System.currentTimeMillis() + 1000 * 60 * 2;
                clearBlock();
                clearChunk();
            }
        }, 1, 1);
    }

    private void taskBlock() {
        Main.scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                clearBlock();
            }
        }, Main.armorStandLimitBlockTaskRefresh * 20L, Main.armorStandLimitBlockTaskRefresh * 20L);
    }

    private void taskChunk() {
        Main.scheduler.runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                clearChunk();
            }
        }, Main.armorStandLimitChunkTaskRefresh * 20L, Main.armorStandLimitChunkTaskRefresh * 20L);
    }

    public void clearBlock() {
        if(isBlockRunning) return;
        isBlockRunning = true;

        for (final World w : Bukkit.getServer().getWorlds()) {
            if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;

            if (w.getLoadedChunks().length < 1) continue;

            Map<Location, List<Entity>> counterBlock = new HashMap<Location, List<Entity>>();
            for (final Chunk c : w.getLoadedChunks()) {
                try {
                    if (c.getEntities() == null || c.getEntities().length < 1) continue;

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
                                Bukkit.getScheduler().runTask(Main.instance, () -> {
                                    try {
                                        ArmorStand arm = (ArmorStand) e;
                                        arm.remove();
                                    } catch (Exception ignored) {
                                    }
                                });
                            }
                        }
                    });

                    counterBlock.clear();
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        }
        isBlockRunning = false;
    }

    public void clearChunk() {
        if(isChunkRunning) return;
        isChunkRunning = true;

        for (final World w : Bukkit.getServer().getWorlds()) {
            if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;
            if (w.getLoadedChunks().length < 1) {
                continue;
            }

            Map<Chunk, List<Entity>> counterChunk = new HashMap<Chunk, List<Entity>>();
            for (final Chunk c : w.getLoadedChunks()) {
                try {
                    if (c.getEntities() == null || c.getEntities().length < 1) continue;
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
                            Bukkit.getScheduler().runTask(Main.instance, () -> {
                                try {
                                    ArmorStand arm = (ArmorStand) e;
                                    arm.remove();
                                } catch (Exception ignored) {
                                }
                            });
                        }
                    }
                });
                counterChunk.clear();
            }
        }
        isChunkRunning = false;
    }
}
