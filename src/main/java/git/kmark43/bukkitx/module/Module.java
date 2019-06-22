package git.kmark43.bukkitx.module;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a module of code that can be enabled or disabled
 */
public abstract class Module implements Listener {
    /**
     * Enables several modules in the order they're given
     * @param plugin The plugin enabling the modules
     * @param modules The modules to enable
     */
    public static void enableAll(JavaPlugin plugin, Module... modules) {
        for (int i = 0; i < modules.length; i++) {
            modules[i].enable(plugin);
        }
    }

    /**
     * Disables several modules in the reverse of the order they're given
     * @param modules The modules to disable
     */
    public static void disableAll(Module... modules) {
        for (int i = modules.length - 1; i >= 0; i--) {
            modules[i].disable();
        }
    }

    private boolean enabled;

    /**
     * Enables the given module
     * @param plugin The plugin enabling the module
     */
    public void enable(JavaPlugin plugin) {
        if (!enabled) {
            enabled = true;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            onEnable();
        }
    }

    /**
     * Disables the given module
     */
    public void disable() {
        if (enabled) {
            enabled = false;
            HandlerList.unregisterAll(this);
            onDisable();
        }
    }

    /**
     * Handles what to do when this module is enabled
     */
    protected void onEnable() { }

    /**
     * Handles what to do when this module is disabled
     */
    protected void onDisable() { }
}
