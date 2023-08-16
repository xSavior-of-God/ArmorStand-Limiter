package com.xSavior_of_God.ArmorStandLimiter.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.List;

public interface Scheduler {

    public boolean isCurrentlyRunning(int taskId);

    public void cancelTask(int taskId);

    public void cancelTasks(Plugin plugin);

    public boolean isQueued(int taskId);

    public List<?> getActiveWorkers();


    public List<?> getPendingTasks();

    public int runTask(Plugin plugin, Runnable task) throws IllegalArgumentException;

    public int runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException;

    public int runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException;

    public int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException;

    public int runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException;

    public int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException;
}
