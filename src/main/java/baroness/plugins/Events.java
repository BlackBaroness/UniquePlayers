package baroness.plugins;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {

    @EventHandler
    void onJoin(PlayerJoinEvent e) throws IOException {
        Data.registerNew(e.getPlayer().getName());
    }

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent e) {
        if (!Core.getCore().getConfig().getString("command").equals(e.getMessage())) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        if (!p.hasPermission("up.admin")) {
            p.sendMessage(ChatColor.RED + "Недостаточно прав.");
            return;
        }
        List<String> players = Data.getPlayersList();
        if (players == null) {
            say(p, "Не удалось получить список уникальных игроков. Возможно, он был очищен.");
            return;
        }
        int number = 0;
        for (String ignored : players) number++;
        say(p, "Уникальных игроков в базе данных: " + ChatColor.GOLD + number);
        say(p, "Вы можете сохранить список в файл, используя " + ChatColor.GOLD + Data.getCmd());
    }

    @EventHandler
    void onCommandToFile(PlayerCommandPreprocessEvent e) throws IOException {
        if (!e.getMessage().equals(Data.getCmd())) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        say(p, "Тип хранения данных: " + ChatColor.GOLD + Data.type);
        ArrayList<String> names = Data.getPlayersList();
        if (names == null) {
            say(p, "Не удалось получить список уникальных игроков. Возможно, он был очищен.");
            return;
        }
        say(p, "Начинаю выгрузку базы данных уникальных игроков.");
        File storage = new File(Core.getCore().getDataFolder() + File.separator + "storage.yml");
        BufferedWriter bw = new BufferedWriter(new FileWriter(storage));
        bw.write(names.toString());
        bw.close();
        say(p, "Итоговый файл: " + ChatColor.GOLD + "/plugins/UniquePlayers/storage.yml");
    }

    void say(Player p, String msg) {
        p.sendMessage(ChatColor.GOLD + "UniquePlayers " + ChatColor.AQUA + "" + ChatColor.BOLD + "| " + ChatColor.RESET + ChatColor.GRAY + msg);
    }
}
