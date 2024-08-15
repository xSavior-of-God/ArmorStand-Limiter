package com.xSavior_of_God.ArmorStandLimiter.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class SchedulerBukkit implements Scheduler {
    public boolean isCurrentlyRunning(int taskId) {
        return Bukkit.getScheduler().isCurrentlyRunning(taskId);
    }

    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public void cancelTasks(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    public boolean isQueued(int taskId) {
        return Bukkit.getScheduler().isQueued(taskId);
    }

    public List<?> getActiveWorkers() {
        return Bukkit.getScheduler().getActiveWorkers();
    }

    public List<?> getPendingTasks() {
        return Bukkit.getScheduler().getPendingTasks();
    }

    public int runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTask(plugin, task).getTaskId();
    }

    public int runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task).getTaskId();
    }

    public int runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay).getTaskId();
    }

    public int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay).getTaskId();
    }

    public int runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period).getTaskId();
    }

    public int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period).getTaskId();
    }

    public int runAsRegionScheduler(Plugin plugin, Location location, Runnable task, boolean isAsync) throws IllegalArgumentException {
        if(isAsync)
            return this.runTaskAsynchronously(plugin, task);
        return this.runTask(plugin, task);
    }

    public int runAsRegionScheduler(Plugin plugin, Chunk chunk, Runnable task, boolean isAsync) throws IllegalArgumentException {
        if(isAsync)
            return this.runTaskAsynchronously(plugin, task);
        return this.runTask(plugin, task);
    }

    public boolean isPrimaryThread() {
        return Bukkit.isPrimaryThread();
    }
}
