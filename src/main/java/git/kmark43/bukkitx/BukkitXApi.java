package git.kmark43.bukkitx;

import git.kmark43.bukkitx.cooldown.CooldownManager;

public class BukkitXApi {
    public static CooldownManager getCooldownManager() {
        return BukkitX.getInstance().getModule(CooldownManager.class);
    }
}
