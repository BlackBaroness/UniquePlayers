package ru.sapphirelife;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UniquePlayers extends JavaPlugin implements Listener, CommandExecutor {

    private boolean onlyOnDisable;
    private boolean saveData;
    private Collector data;
    private String permission;

    @Override
    public void onEnable() {
        if (!new File(getDataFolder() + File.separator + "config.yml").exists()) saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("up").setExecutor(this);
        loadData();
        loadConfiguration();
        log("Enabled!");
    }


    @Override
    public void onDisable() {
        if (onlyOnDisable) save();
    }

    private void loadData() {
        try {
            File file = new File(getDataFolder() + File.separator + "data");
            ObjectInputStream ois;
            ois = new ObjectInputStream(new FileInputStream(file));
            data = (Collector) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            data = new Collector();
        }
    }

    private void loadConfiguration() {
        reloadConfig();
        FileConfiguration cfg = getConfig();

        saveData = cfg.getBoolean("save_data");
        log("Save data: " + getColorByBoolean(saveData) + saveData);

        if (saveData) {
            onlyOnDisable = cfg.getBoolean("save_only_on_disable");
            log("Save only on disable: " + getColorByBoolean(onlyOnDisable) + onlyOnDisable);
        }

        permission = cfg.getString("permission");
        log("Permission to /up command: " + ChatColor.GREEN + permission);
    }

    @Override
    public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
        if (!p.hasPermission(permission)) {
            say(p, ChatColor.RED + "Недостаточно прав для выполнения данной команды.");
            return true;
        }

        if (args.length == 0) {
            help(p);
            return true;
        }

        if (args[0].equals("wipe")) {
            wipe(p, args);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equals("stats")) {
                say(p, "Уникальных игроков в базе данных: " + ChatColor.GOLD + data.getData().size());
                return true;
            }
            if (args[0].equals("tofile")) {
                say(p, "Начинаю выгрузку базы данных уникальных игроков.");
                try {
                    File storage = new File(getDataFolder() + File.separator + "storage.yml");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(storage));
                    for (PlayerData playerData : data.getData()) {
                        bw.write(playerData.toString());
                        bw.newLine();
                    }
                    bw.close();
                    say(p, "Итоговый файл: " + ChatColor.GOLD + "/plugins/UniquePlayers/storage.yml");
                } catch (IOException e) {
                    say(p, ChatColor.RED + "Что-то пошло не так.");
                }
                return true;
            }
            if (args[0].equals("info")) {
                help(p);
                return true;
            }
            if (args[0].equals("reload")) {
                loadConfiguration();
                say(p, "Плагин перезагружен.");
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equals("info")) {
                say(p, "Сбор данных об игроке...");

                String nick = args[1];
                boolean success = false;
                PlayerData PLAYER_DATA = null;

                for (PlayerData playerData : data.getData()) {
                    if (playerData.getNick().equals(nick)) {
                        success = true;
                        PLAYER_DATA = playerData;
                        break;
                    }
                }

                if (!success) {
                    say(p, "Игрок с ником " + nick + " не найден.");
                    return true;
                }
                boolean online = Bukkit.getPlayer(nick) != null;

                say(p, "==== " + ChatColor.GOLD + "Информация об игроке " + nick + ChatColor.GRAY + " ====");
                say(p, "Дата регистрации: " + PLAYER_DATA.getDate());
                if (online) {
                    say(p, "Сейчас " + ChatColor.GREEN + "онлайн");
                } else {
                    say(p, "Сейчас " + ChatColor.RED + "оффлайн.");
                }

                StringBuilder builder = new StringBuilder();
                int amount = 26 + nick.length();
                for (int i = 0; i < amount; i++) {
                    builder.append("=");
                }
                say(p, builder.toString());
                return true;
            }
        }
        help(p);
        return true;
    }

    private void wipe(CommandSender p, String[] args) {
        if (args.length != 2) {
            say(p, ChatColor.RED + "Вы уверены, что хотите очистить данные плагина?");
            say(p, ChatColor.RED + "Данное действие необратимо!");
            say(p, ChatColor.RED + "Для подтверждения введите " + ChatColor.GOLD + "/up wipe " + p.getName());
            return;
        }

        if (args[1].equals(p.getName())) {
            if (saveData) {
                data.wipe();
                save();
            } else {
                data.forceWipe();
            }


            say(p, "Данные UniquePlayers очищены.");
        } else {
            say(p, "Некорректное подтверждение!");
        }
    }

    private void help(CommandSender p) {
        say(p, ChatColor.GRAY + "/up " + ChatColor.GOLD + "stats" + ChatColor.GRAY + " - просмотр статистики.");
        say(p, ChatColor.GRAY + "/up " + ChatColor.GOLD + "tofile" + ChatColor.GRAY + " - выгрузить все данные в файл.");
        say(p, ChatColor.GRAY + "/up " + ChatColor.GOLD + "info [игрок]" + ChatColor.GRAY + " - получить данные об игроке.");
        say(p, ChatColor.GRAY + "/up " + ChatColor.GOLD + "wipe" + ChatColor.GRAY + " - очистить базу данных.");
        say(p, ChatColor.GRAY + "/up " + ChatColor.GOLD + "reload" + ChatColor.GRAY + " - перезагрузка плагина.");
    }

    private void say(CommandSender p, String msg) {
        p.sendMessage(ChatColor.GOLD + "UniquePlayers " + ChatColor.AQUA + "" + ChatColor.BOLD + "| " + ChatColor.RESET + ChatColor.GRAY + msg);
    }

    private void save() {
        if (saveData) {
            SaveThread thread = new SaveThread(data, this);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String nick = e.getPlayer().getName();
                boolean progress = false;

                for (PlayerData playerData : data.getData()) {
                    if (playerData.getNick().equals(nick)) {
                        progress = true;
                        break;
                    }
                }

                if (progress) return;

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                String dateString = format.format(date);
                data.addNew(new PlayerData(nick, dateString));
                save();
            }
        }.runTask(this);
    }

    private void log(String s) {
        System.out.println(ChatColor.AQUA + "[UniquePlayers] " + ChatColor.WHITE + s);
    }

    private ChatColor getColorByBoolean(Boolean b) {
        if (b) return ChatColor.GREEN;
        return ChatColor.RED;
    }
}
