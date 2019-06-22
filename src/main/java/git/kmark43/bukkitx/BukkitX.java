package git.kmark43.bukkitx;

import git.kmark43.bukkitx.cooldown.CooldownManager;
import git.kmark43.bukkitx.manager.ManagerHandler;
import git.kmark43.bukkitx.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitX extends JavaPlugin {
    private static BukkitX instance;

    public static BukkitX getInstance() {
        return instance;
    }

    private ManagerHandler managerHandler;

    public <T extends Module> T getModule(Class<T> clazz) {
        return managerHandler.getModule(clazz);
    }

    @Override
    public void onEnable() {
        instance = this;
        managerHandler = new ManagerHandler();
        Bukkit.getPluginManager().registerEvents(managerHandler, this);

        managerHandler.register(this,
                new CooldownManager()
        );
    }

    @Override
    public void onDisable() {
        managerHandler = null;
        instance = null;
    }
}
