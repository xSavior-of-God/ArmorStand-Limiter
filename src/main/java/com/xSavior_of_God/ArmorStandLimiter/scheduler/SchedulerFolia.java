package com.xSavior_of_God.ArmorStandLimiter.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.List;

public class SchedulerFolia implements Scheduler {

    public boolean isCurrentlyRunning(int taskId) {
        return false;
    }

    public void cancelTask(int taskId) {

    }

    public void cancelTasks(Plugin plugin) {

    }

    public boolean isQueued(int taskId) {
        return false;
    }

    public List<?> getActiveWorkers() {
        return null;
    }


    public List<?> getPendingTasks() {
        return null;
    }

    public void runTask(Plugin plugin, Runnable task) throws IllegalArgumentException {

    }

    public void runTaskAsynchronously(Plugin plugin, Runnable task) throws IllegalArgumentException {

    }

    public void runTaskLater(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {
    }

    public void runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) throws IllegalArgumentException {

    }

    public void runTaskTimer(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {

    }

    public void runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) throws IllegalArgumentException {

    }
}
