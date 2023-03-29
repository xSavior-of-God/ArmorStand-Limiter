package com.xSavior_of_God.ArmorStandLimiter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Checker {
    long myTime = System.currentTimeMillis();
    private static Map<Location, Integer> localCounterBlock = new HashMap<Location, Integer>();
    private static Map<Chunk, Integer> localCounterChunk = new HashMap<Chunk, Integer>();

    public void timerTask() {
        if (Main.TPSMeterEnabled) TPSMeterTask();
        if (Main.armorStandLimitBlockTaskEnabled) taskBlock();
        if (Main.armorStandLimitChunkTaskEnabled) taskChunk();
    }

    public void TPSMeterTask() {
        Bukkit.getScheduler().runTaskTimer(Main.instance, new Runnable() {
            @Override
            public void run() {
                double TPS = 20.0;

                try {
                    Class<?> craftServer = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftServer");
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
            }

        }, 1, 1);
    }

    private void taskBlock() {
        Main.instance.getServer().getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Main.counterBlock.clear();
                for (final World w : Bukkit.getServer().getWorlds()) {
                    if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;

                    if (w.getLoadedChunks().length < 1) continue;


                    for (final Chunk c : w.getLoadedChunks()) {
                        try {
                            if (c.getEntities() == null || c.getEntities().length < 1) continue;

                            for (final Entity e : c.getEntities()) {
                                if (!(e instanceof ArmorStand)) {
                                    continue;
                                }

                                final ArmorStand arm = (ArmorStand) e;
                                if (Utils.checkArmorStand(arm)) continue;

                                Location loc = arm.getLocation();

                                loc.setY(0);
                                int x = (int) loc.getX();
                                int z = (int) loc.getZ();
                                loc.setX(x);
                                loc.setZ(z);
                                loc.setPitch(0);
                                loc.setYaw(0);
                                Vector vec = new Vector();
                                loc.setDirection(vec);

                                if (Main.counterBlock.containsKey(loc)) {
                                    Main.counterBlock.replace(loc, (Main.counterBlock.get(loc) + 1));
                                } else {
                                    Main.counterBlock.put(loc, 1);
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }

                Main.counterBlock.forEach((location, counter) -> {
                    if (counter >= Main.armorStandLimitBlockTrigger) {
                        Notifications.send(location, counter);

                        if (!Bukkit.isPrimaryThread()) {
                            Bukkit.getScheduler().runTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    for (Entity e : location.getWorld().getEntities()) {
                                        if (!(e instanceof ArmorStand)) {
                                            continue;
                                        }
                                        final ArmorStand arm = (ArmorStand) e;
                                        if (Utils.checkArmorStand(arm)) continue;
                                        Location loc = arm.getLocation();

                                        loc.setY(0);
                                        int x = (int) loc.getX();
                                        int z = (int) loc.getZ();
                                        loc.setX(x);
                                        loc.setZ(z);
                                        loc.setPitch(0);
                                        loc.setYaw(0);
                                        Vector vec = new Vector();
                                        loc.setDirection(vec);

                                        if (loc.toString().equalsIgnoreCase(location.toString())) {
                                            try {
                                                arm.remove();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                            Main.counterBlock.replace(loc, (Main.counterBlock.get(loc) - 1));
                                        }
                                    }
                                }
                            });
                        } else {
                            for (Entity e : location.getWorld().getEntities()) {
                                if (!(e instanceof ArmorStand)) {
                                    continue;
                                }
                                final ArmorStand arm = (ArmorStand) e;
                                if (Utils.checkArmorStand(arm)) continue;
                                Location loc = arm.getLocation();

                                loc.setY(0);
                                int x = (int) loc.getX();
                                int z = (int) loc.getZ();
                                loc.setX(x);
                                loc.setZ(z);
                                loc.setPitch(0);
                                loc.setYaw(0);
                                Vector vec = new Vector();
                                loc.setDirection(vec);

                                if (loc.toString().equalsIgnoreCase(location.toString())) {
                                    try {
                                        arm.remove();
                                    } catch (Exception ignored) {
                                    }
                                    Main.counterBlock.replace(loc, (Main.counterBlock.get(loc) - 1));
                                }
                            }
                        }


                    }
                });
            }

        }, Main.armorStandLimitBlockTaskRefresh * 60 * 20L, Main.armorStandLimitBlockTaskRefresh * 60 * 20L);
    }

    private void taskChunk() {
        Main.instance.getServer().getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {

            @Override
            public void run() {
                localCounterChunk.clear();
                for (final World w : Bukkit.getServer().getWorlds()) {
                    if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;
                    if (w.getLoadedChunks().length < 1) {
                        continue;
                    }


                    for (final Chunk c : w.getLoadedChunks()) {
                        try {
                            if (c.getEntities() == null || c.getEntities().length < 1) continue;
                            for (final Entity e : c.getEntities()) {
                                if (!(e instanceof ArmorStand)) {
                                    continue;
                                }
                                final ArmorStand arm = (ArmorStand) e;
                                if (Utils.checkArmorStand(arm)) continue;
                                Chunk chunk = arm.getLocation().getChunk();
                                if (localCounterChunk.containsKey(chunk)) {
                                    localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) + 1));
                                } else {
                                    localCounterChunk.put(chunk, 1);
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                }

                localCounterChunk.forEach((chunk, counter) -> {
                    if (counter >= Main.armorStandLimitChunkTrigger) {
                        Notifications.send(chunk, counter);
                        if (!Bukkit.isPrimaryThread()) {
                            Bukkit.getScheduler().runTask(Main.instance, new Runnable() {
                                @Override
                                public void run() {
                                    for (Entity e : chunk.getWorld().getEntities()) {
                                        if (!(e instanceof ArmorStand)) {
                                            continue;
                                        }
                                        final ArmorStand arm = (ArmorStand) e;
                                        if (Utils.checkArmorStand(arm)) continue;
                                        Chunk mychunk = arm.getLocation().getChunk();
                                        if (chunk.toString().equalsIgnoreCase(mychunk.toString())) {
                                            try {
                                                arm.remove();
                                            } catch (Exception ignored) {
                                            }
                                            localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) - 1));
                                        }
                                    }
                                }
                            });
                        } else {
                            for (Entity e : chunk.getWorld().getEntities()) {
                                if (!(e instanceof ArmorStand)) {
                                    continue;
                                }
                                final ArmorStand arm = (ArmorStand) e;
                                if (Utils.checkArmorStand(arm)) continue;
                                Chunk mychunk = arm.getLocation().getChunk();
                                if (chunk.toString().equalsIgnoreCase(mychunk.toString())) {
                                    try {
                                        arm.remove();
                                    } catch (Exception ignored) {
                                    }
                                    localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) - 1));
                                }
                            }
                        }
                    }
                });
            }

        }, Main.armorStandLimitChunkTaskRefresh * 60 * 20L, Main.armorStandLimitChunkTaskRefresh * 60 * 20L);
    }

    public void clearBlock() {
        localCounterBlock.clear();
        for (final World w : Bukkit.getServer().getWorlds()) {
            if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;
            if (w.getLoadedChunks() == null || w.getLoadedChunks().length < 1) continue;
            for (final Chunk c : w.getLoadedChunks()) {
                if (c.getEntities() == null || c.getEntities().length < 1) continue;
                try {
                    for (final Entity e : c.getEntities()) {
                        if (!(e instanceof ArmorStand)) {
                            continue;
                        }
                        final ArmorStand arm = (ArmorStand) e;
                        if (Utils.checkArmorStand(arm)) continue;
                        Location loc = arm.getLocation();

                        loc.setY(0);
                        int x = (int) loc.getX();
                        int z = (int) loc.getZ();
                        loc.setX(x);
                        loc.setZ(z);
                        loc.setPitch(0);
                        loc.setYaw(0);
                        Vector vec = new Vector();
                        loc.setDirection(vec);

                        if (localCounterBlock.containsKey(loc)) {
                            localCounterBlock.replace(loc, (localCounterBlock.get(loc) + 1));
                        } else {
                            localCounterBlock.put(loc, 1);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        localCounterBlock.forEach((location, counter) -> {
            if (counter >= Main.armorStandLimitBlockTrigger) {
                Notifications.send(location, counter);
                for (Entity e : location.getWorld().getEntities()) {
                    if (!(e instanceof ArmorStand)) {
                        continue;
                    }
                    final ArmorStand arm = (ArmorStand) e;
                    if (Utils.checkArmorStand(arm)) continue;
                    Location loc = arm.getLocation();

                    loc.setY(0);
                    int x = (int) loc.getX();
                    int z = (int) loc.getZ();
                    loc.setX(x);
                    loc.setZ(z);
                    loc.setPitch(0);
                    loc.setYaw(0);
                    Vector vec = new Vector();
                    loc.setDirection(vec);

                    if (loc.toString().equalsIgnoreCase(location.toString())) {
                        try {
                            arm.remove();
                        } catch (Exception ignored) {
                        }
                        localCounterBlock.replace(loc, (localCounterBlock.get(loc) - 1));
                    }
                }
            }
        });
    }

    public void clearChunk() {
        Main.ChecksDisabledWorlds.clear();
        for (final World w : Bukkit.getServer().getWorlds()) {
            if (Main.ChecksDisabledWorlds.contains(w.getName())) continue;
            if (w.getLoadedChunks() == null || w.getLoadedChunks().length < 1) continue;
            for (final Chunk c : w.getLoadedChunks()) {
                try {
                    if (c.getEntities() == null || c.getEntities().length < 1) continue;
                    for (final Entity e : c.getEntities()) {
                        if (!(e instanceof ArmorStand)) {
                            continue;
                        }
                        final ArmorStand arm = (ArmorStand) e;
                        if (Utils.checkArmorStand(arm)) continue;
                        Chunk chunk = arm.getLocation().getChunk();
                        if (localCounterChunk.containsKey(chunk)) {
                            localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) + 1));
                        } else {
                            localCounterChunk.put(chunk, 1);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        }

        localCounterChunk.forEach((chunk, counter) -> {
            if (counter >= Main.armorStandLimitChunkTrigger) {
                Notifications.send(chunk, counter);
                for (Entity e : chunk.getWorld().getEntities()) {
                    if (!(e instanceof ArmorStand)) {
                        continue;
                    }
                    final ArmorStand arm = (ArmorStand) e;
                    if (Utils.checkArmorStand(arm)) continue;
                    Chunk mychunk = arm.getLocation().getChunk();
                    if (chunk.toString().equalsIgnoreCase(mychunk.toString())) {
                        try {
                            arm.remove();
                        } catch (Exception ignored) {
                        }
                        localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) - 1));
                    }
                }
            }
        });
    }
}
