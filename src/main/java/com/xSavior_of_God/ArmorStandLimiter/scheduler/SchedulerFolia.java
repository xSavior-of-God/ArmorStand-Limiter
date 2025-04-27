package com.xSavior_of_God.ArmorStandLimiter.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerFolia implements Scheduler {
    Map<Integer, ScheduledTask> tasks = new HashMap<>();
    int counter = 0;
    Plugin p = null;

    public SchedulerFolia(Plugin plugin) {
        p = plugin;
    }

    /**
     * @param taskId Not supported by Folia
     */
    public boolean isCurrentlyRunning(int taskId) {
        // TODO Log warning "Folia does not support isCurrentlyRunning(int taskId)"
        return false;
    }

    /**
     * @param taskId Not supported by Folia
     */
    public void cancelTask(int taskId) {
        // TODO Log warning "Folia does not support cancelTask(int taskId)"
    }

    public void cancelTasks(Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
        Bukkit.getAsyncScheduler().cancelTasks(plugin);
    }

    /**
     * @param taskId Not supported by Folia
     */
    public boolean isQueued(int taskId) {
        // TODO Log warning "Folia does not support isQueued(int taskId)"
        return false;
    }

    /**
     * Not supported by Folia
     */
    public List<?> getActiveWorkers() {
        // TODO Log warning "Folia does not support getActiveWorkers()"
        return null;
    }

    /**
     * Not supported by Folia
     */
    public List<?> getPendingTasks() {
        // TODO Log warning "Folia does not support getPendingTasks()"
        return null;
    }

    public int runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run())
        );
        return counter;
    }

    public int runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getAsyncScheduler().runNow(plugin, t -> task.run())
        );
        return counter;
    }

    public int runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), delay)
        );
        return counter;

    }

    public int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getAsyncScheduler().runDelayed(plugin, t -> task.run(), delay, TimeUnit.SECONDS)
        );
        return counter;

    }

    public int runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), delay, period)
        );
        return counter;

    }

    public int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        tasks.put(
                counter++,
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), delay, period)
        );
        return counter;
    }

    public int runAsRegionScheduler(Plugin plugin, Location location, Runnable task, boolean isAsync) {
        tasks.put(
                counter++,
                Bukkit.getRegionScheduler().run(plugin, location, t -> task.run())
        );
        return counter;
    }

    public int runAsRegionScheduler(Plugin plugin, Chunk chunk, Runnable task, boolean isAsync) {
        tasks.put(
                counter++,
                Bukkit.getRegionScheduler().run(plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), t -> task.run())
        );
        return counter;
    }

    public boolean isPrimaryThread() {
        return true;
    }
}
