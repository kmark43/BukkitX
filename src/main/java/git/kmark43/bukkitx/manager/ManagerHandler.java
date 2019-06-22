package git.kmark43.bukkitx.manager;

import git.kmark43.bukkitx.module.Module;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ManagerHandler implements Listener {
    private Map<Class<? extends Module>, Module> moduleMap;
    private Map<Class<? extends Plugin>, List<Class<? extends Module>>> pluginMap;

    public ManagerHandler() {
        moduleMap = new HashMap<>();
        pluginMap = new HashMap<>();
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return clazz.cast(moduleMap.get(clazz));
    }

    public void register(JavaPlugin plugin, Module... modules) {
        for (Module module : modules) {
            moduleMap.put(module.getClass(), module);
        }

        List<Class<? extends Module>> moduleSet = Stream.of(modules)
                .map(Module::getClass)
                .collect(Collectors.toList());

        if (pluginMap.containsKey(plugin.getClass())) {
            pluginMap.get(plugin.getClass()).addAll(moduleSet);
        } else {
            pluginMap.put(plugin.getClass(), moduleSet);
        }

        Module.enableAll(plugin, modules);
    }

    public void unregisterAll(JavaPlugin plugin) {

    }

    public void unregister(Class<? extends Module>... moduleClasses) {
        Module[] modules = getModules(moduleClasses);
        unregister(modules);
    }

    public void unregister(Module... modules) {
        Module.disableAll(modules);
        for (Module module : modules) {
            moduleMap.remove(module.getClass());
        }
    }

    private Module[] getModules(Class<? extends Module>... moduleClasses) {
        return Stream.of(moduleClasses).map(moduleMap::get).toArray(Module[]::new);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        if (pluginMap.containsKey(e.getPlugin().getClass())) {
            List<Class<? extends Module>> moduleClasses = pluginMap.remove(e.getPlugin().getClass());
            Module[] modules = new Module[moduleClasses.size()];
            for (int i = 0; i < moduleClasses.size(); i++) {
                modules[i] = moduleMap.remove(moduleClasses.get(i));
            }
            Module.disableAll(modules);
        }
    }
}
