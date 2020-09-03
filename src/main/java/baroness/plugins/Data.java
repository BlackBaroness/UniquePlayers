package baroness.plugins;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Data {

    static String type;
    static final Core core = Core.getCore();
    static ArrayList<String> virtual = new ArrayList<>();
    private static String cmd;

    public static boolean readType() {
        String type = core.getConfig().getString("storage");
        if (!type.equals("file") && !type.equals("virtual") &&  !type.equals("hybrid")) {
            Core.error("Invalid storage type! Disabling the plugin...");
            return true;
        }
        Core.log("Selected storage type: " + type);
        Data.type = type;
        if (type.equals("hybrid")) {
            readHybrid();
        }
        cmd = core.getConfig().getString("command") + " tofile";
        return false;
    }

    private static void readHybrid() {
        File data = new File(core.getDataFolder() + File.separator + "data");
        if (!data.exists()) {
            createDir(data);
            return;
        }
        File[] files = data.listFiles();
        if (files == null) return;
        for (File file : files) {
            virtual.add(file.getName());
        }
    }

    public static void registerNew(String name) throws IOException {
        if (type.equals("virtual") || type.equals("hybrid")) {
            if (virtual.contains(name)) return;
            virtual.add(name);
            return;
        }
        if (type.equals("file")) {
            File data = new File(core.getDataFolder() + File.separator + "data");
            if (!data.exists()) {
                createDir(data);
                return;
            }
            if (isRegistered(data, name)) return;
            register(data, name);
        }
    }

    private static void createDir(File data) {
        if (data.mkdir()) {
            Core.log("Data dir created");
        }
    }

    private static void register(File data, String name) throws IOException {
        File player = new File(data + File.separator + name);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(player);
        cfg.createSection("-");
        cfg.save(player);
    }

    private static boolean isRegistered(File data, String name) {
        File[] files = data.listFiles();
        List<String> names = new ArrayList<>();
        if (files == null) return false;
        for (File file : files) {
            names.add(file.getName());
        }
        return names.contains(name);
    }

    public static ArrayList<String> getPlayersList() {
        if (type.equals("virtual") || type.equals("hybrid")) {
            return virtual;
        }
        if (type.equals("file")) {
            File data = new File(core.getDataFolder() + File.separator + "data");
            if (!data.exists()) {
                createDir(data);
                return null;
            }
            File[] files = data.listFiles();
            ArrayList<String> names = new ArrayList<>();
            if (files == null) return null;
            for (File file : files) {
                names.add(file.getName());
            }
            return names;
        }
        return null;
    }

    public static String getCmd() {
        return cmd;
    }

    public static void saveHybrid() throws IOException {
        File data = new File(core.getDataFolder() + File.separator + "data");
        for (String name : virtual) {
            register(data, name);
        }
    }
}
