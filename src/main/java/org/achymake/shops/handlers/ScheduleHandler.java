package org.achymake.shops.handlers;

import org.achymake.shops.Shops;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class ScheduleHandler {
    private Shops getInstance() {
        return Shops.getInstance();
    }
    private BukkitScheduler getBukkitScheduler() {
        return getInstance().getBukkitScheduler();
    }
    public BukkitTask runTaskLater(Runnable runnable, long timer) {
        return getBukkitScheduler().runTaskLater(getInstance(), runnable, timer);
    }
    public BukkitTask runTaskAsynchronously(Runnable runnable) {
        return getBukkitScheduler().runTaskAsynchronously(getInstance(), runnable);
    }
    public boolean isQueued(int taskID) {
        return getBukkitScheduler().isQueued(taskID);
    }
    public void cancel(int taskID) {
        if (!isQueued(taskID))return;
        getBukkitScheduler().cancelTask(taskID);
    }
    public void disable() {
        getBukkitScheduler().cancelTasks(getInstance());
    }
}