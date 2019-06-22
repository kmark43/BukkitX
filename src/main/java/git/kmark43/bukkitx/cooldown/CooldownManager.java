package git.kmark43.bukkitx.cooldown;

import git.kmark43.bukkitx.module.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility class to help manage cooldowns for certain
 * actions
 */
public class CooldownManager extends Module {
    private static class CooldownTask {
        private String key;
        private long startTime;
        private int delay;
        private BukkitTask task;
        private BukkitTask endTask;

        public CooldownTask(String key, long startTime, int delay) {
            this.key = key;
            this.startTime = startTime;
            this.delay = delay;
        }
    }

    protected void onDisable() {
        cancelAllTasks();
    }

    private Map<String, CooldownTask> taskCooldowns;

    /**
     * Constructs a new cooldown manager
     */
    public CooldownManager() {
        taskCooldowns = new HashMap<>();
    }

    /**
     * Starts a cooldown task with the given key if there is no other task
     * active with the same key
     * @param plugin The plugin to use for the task
     * @param key The key of the cooldown task
     * @param delay The delay in ticks before the cooldown is finished
     * @return true if the task was started, false if there is another task not finished
     */
    public boolean startIfAvailable(JavaPlugin plugin, String key, int delay) {
        return startIfAvailable(plugin, key, delay, null);
    }

    /**
     * Starts a cooldown task with the given key if there is no other task
     * active with the same key
     * @param plugin The plugin to use for the task
     * @param key The key of the cooldown task
     * @param delay The delay in ticks before the cooldown is finished
     * @param onEnd A task to run when the cooldown is finished
     * @return true if the task was started, false if there is another task not finished
     */
    public boolean startIfAvailable(JavaPlugin plugin, String key, int delay, Runnable onEnd) {
        if (taskCooldowns.containsKey(key)) {
            return false;
        }
        cooldown(plugin, key, delay, onEnd);
        return true;
    }

    /**
     * Starts a cooldown task with the given key. Overwrites any task already
     * running with the same key
     * @param plugin The plugin to use for the task
     * @param key The key of the cooldown task
     * @param delay The delay in ticks before the cooldown is finished
     */

    public void cooldown(JavaPlugin plugin, String key, int delay) {
        cooldown(plugin, key, delay, null);
    }

    /**
     * Starts a cooldown task with the given key. Overwrites any task already
     * running with the same key
     * @param plugin The plugin to use for the task
     * @param key The key of the cooldown task
     * @param delay The delay in ticks before the cooldown is finished
     * @param onEnd A task to run when the cooldown is finished
     */
    public void cooldown(JavaPlugin plugin, String key, int delay, Runnable onEnd) {
        if (taskCooldowns.containsKey(key)) {
            cancelTask(key);
        }

        CooldownTask task = new CooldownTask(key, System.currentTimeMillis(), delay);
        task.task = Bukkit.getScheduler().runTaskLater(plugin, () -> clearTask(key), delay);
        task.endTask = Bukkit.getScheduler().runTaskLater(plugin, onEnd, delay);
        taskCooldowns.put(key, task);
    }

    /**
     * @param key The task key
     * @return true if the task is finished, false if it's still running
     */
    public boolean isFinished(String key) {
        return !taskCooldowns.containsKey(key);
    }

    /**
     * @param key The task key
     * @return The percentage of the cooldown time completed since starting
     */
    public double getPercentFinished(String key) {
        if (!taskCooldowns.containsKey(key)) {
            return 1;
        }
        CooldownTask task = taskCooldowns.get(key);
        long elapsed = System.currentTimeMillis() - task.startTime;
        int ticks = (int)(elapsed / 20);
        return (double)Math.min(ticks / task.delay, 1);
    }

    private void clearTask(String key) {
        CooldownTask task = taskCooldowns.remove(key);
        if (task == null) {
            return;
        }

        task.task.cancel();
    }

    /**
     * Cancels any tasks running with the given key
     * @param key The task key
     */
    public void cancelTask(String key) {
        CooldownTask task = taskCooldowns.remove(key);
        if (task == null) {
            return;
        }

        task.task.cancel();

        if (task.endTask != null) {
            task.endTask.cancel();
        }
    }

    /**
     * Cancels all running tasks
     */
    public void cancelAllTasks() {
        for (String key : new ArrayList<>(taskCooldowns.keySet())) {
            cancelTask(key);
        }
    }
}
