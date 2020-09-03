package baroness.plugins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Core extends JavaPlugin {

    private static Core core;

    @Override
    public void onEnable() {
        File cfg = new File(getDataFolder() + File.separator + "config.yml");
        if (!cfg.exists()) {
            saveDefaultConfig();
        }
        if (Data.readType()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        log("Enabled!");
    }

    @Override
    public void onLoad() {
        core = this;
    }

    @Override
    public void onDisable() {
        if (Data.type == null) return;
        if (!Data.type.equals("hybrid")) return;
        try {
            Data.saveHybrid();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Core getCore() {
        return core;
    }

    public static void log(String msg) {
        System.out.println(ChatColor.GREEN + "[UniquePlayers] " + ChatColor.WHITE + msg);
    }

    public static void error(String msg) {
        System.out.println(ChatColor.GREEN + "[UniquePlayers] " + ChatColor.RED + msg);
    }
}
