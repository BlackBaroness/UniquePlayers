package ru.sapphirelife;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SaveThread extends Thread {

    private final Collector collector;
    private final JavaPlugin core;

    protected SaveThread(Collector collector, JavaPlugin core) {
        this.core = core;
        this.collector = collector;
        start();
    }

    @Override
    public synchronized void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File file = new File(core.getDataFolder() + File.separator + "data");
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                    oos.writeObject(collector);
                    oos.close();
                } catch (IOException e) {
                    System.out.println(ChatColor.AQUA + "[UniquePlayers] " + ChatColor.RED + "Save task failed!");
                }
            }
        }.runTask(core);
    }
}
