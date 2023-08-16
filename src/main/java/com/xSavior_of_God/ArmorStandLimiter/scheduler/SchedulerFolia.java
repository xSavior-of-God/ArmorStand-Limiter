package com.xSavior_of_God.ArmorStandLimiter.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SchedulerFolia implements Scheduler {

    /**
     * @param taskId
     * Not supported by Folia
     */
    public boolean isCurrentlyRunning(int taskId) {
        // TODO Log warning "Folia does not support isCurrentlyRunning(int taskId)"
        return false;
    }

    /**
     * @param taskId
     * Not supported by Folia
     */
    public void cancelTask(int taskId) {
        // TODO Log warning "Folia does not support cancelTask(int taskId)"
    }

    public void cancelTasks(Plugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
        Bukkit.getAsyncScheduler().cancelTasks(plugin);
    }

    /**
     * @param taskId
     * Not supported by Folia
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
        return Bukkit.getAsyncScheduler().runNow(plugin, task);
    }

    public int runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {
        Bukkit.getGlobalRegionScheduler().run(this, () -> {
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, 1L, 1L, TimeUnit.SECONDS);
        });
    }

    public int runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {

    }

    public void runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {

    }

    public int runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {

    }

    public int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {

    }
}
