package com.xSavior_of_God.ArmorStandLimiter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.xSavior_of_God.ArmorStandLimiter.notifications.Notifications;

public class Checker {

	long myTime = System.currentTimeMillis();
	private static Map<Location, Integer> localCounterBlock = new HashMap<Location, Integer>();
	private static Map<Chunk, Integer> localCounterChunk = new HashMap<Chunk, Integer>();

	public void timerTask() {
		if (Main.TPSMeterEnabled)
			TPSMeterTask();
		if (Main.armorStandLimitBlockTaskEnabled)
			taskBlock();
		if (Main.armorStandLimitChunkTaskEnabled)
			taskChunk();

	}

	public void TPSMeterTask() {
		Bukkit.getScheduler().runTaskTimer(Main.instance, new Runnable() {

			@Override
			public void run() {
				Double TPS = 20.0;
				Object serverInstance;

				try {
					serverInstance = Utilis.getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
					Field tpsField = serverInstance.getClass().getField("recentTps");
					double[] tps = ((double[]) tpsField.get(serverInstance));
					TPS = tps[0];
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				    | SecurityException | NoSuchFieldException e) {
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
				for (final World w : Bukkit.getServer().getWorlds()) {
					for (final Entity e : w.getEntities()) {
						if (!(e instanceof ArmorStand)) {
							continue;
						}
						final ArmorStand arm = (ArmorStand) e;
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
				}

				Main.counterBlock.forEach((location, counter) -> {
					if (counter >= Main.armorStandLimitBlockTrigger) {
						Notifications.send(location, counter);
						for (Entity e : location.getWorld().getEntities()) {
							if (!(e instanceof ArmorStand)) {
								continue;
							}
							final ArmorStand arm = (ArmorStand) e;
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
			}

		}, Main.armorStandLimitBlockTaskRefersh * 60 * 20L, Main.armorStandLimitBlockTaskRefersh * 60 * 20L);
	}

	private void taskChunk() {
		Main.instance.getServer().getScheduler().runTaskTimerAsynchronously(Main.instance, new Runnable() {

			@Override
			public void run() {
				for (final World w : Bukkit.getServer().getWorlds()) {
					for (final Entity e : w.getEntities()) {
						if (!(e instanceof ArmorStand)) {
							continue;
						}
						final ArmorStand arm = (ArmorStand) e;
						Chunk chunk = arm.getLocation().getChunk();
						if (localCounterChunk.containsKey(chunk)) {
							localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) + 1));
						} else {
							localCounterChunk.put(chunk, 1);
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
							Chunk mychunk = arm.getLocation().getChunk();
							if (chunk.toString().equalsIgnoreCase(mychunk.toString())) {
								try {
									arm.remove();
								} catch (Exception ex) {
								}
								localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) - 1));
							}
						}
					}
				});
			}

		}, Main.armorStandLimitChunkTaskRefersh * 60 * 20L, Main.armorStandLimitChunkTaskRefersh * 60 * 20L);
	}

	public void clearBlock() {
		for (final World w : Bukkit.getServer().getWorlds()) {
			for (final Entity e : w.getEntities()) {
				if (!(e instanceof ArmorStand)) {
					continue;
				}
				final ArmorStand arm = (ArmorStand) e;
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
		}

		localCounterBlock.forEach((location, counter) -> {
			if (counter >= Main.armorStandLimitBlockTrigger) {
				Notifications.send(location, counter);
				for (Entity e : location.getWorld().getEntities()) {
					if (!(e instanceof ArmorStand)) {
						continue;
					}
					final ArmorStand arm = (ArmorStand) e;
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
						}
						localCounterBlock.replace(loc, (localCounterBlock.get(loc) - 1));
					}
				}
			}
		});
	}

	public void clearChunk() {
		for (final World w : Bukkit.getServer().getWorlds()) {
			for (final Entity e : w.getEntities()) {
				if (!(e instanceof ArmorStand)) {
					continue;
				}
				final ArmorStand arm = (ArmorStand) e;
				Chunk chunk = arm.getLocation().getChunk();
				if (localCounterChunk.containsKey(chunk)) {
					localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) + 1));
				} else {
					localCounterChunk.put(chunk, 1);
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
					Chunk mychunk = arm.getLocation().getChunk();
					if (chunk.toString().equalsIgnoreCase(mychunk.toString())) {
						try {
							arm.remove();
						} catch (Exception ex) {
						}
						localCounterChunk.replace(chunk, (localCounterChunk.get(chunk) - 1));
					}
				}
			}
		});
	}

}
