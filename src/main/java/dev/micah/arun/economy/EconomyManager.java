package dev.micah.arun.economy;

import dev.micah.arun.io.YamlFile;
import dev.micah.arun.kits.Kit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class EconomyManager {

    private static YamlFile yamlFile;

    public EconomyManager() {
        yamlFile = new YamlFile("economy");
    }

    public static YamlConfiguration getConfig() {
        return yamlFile.getConfig();
    }
    
    public static void addMoney(Player player, int amount) {
        setMoney(player, getMoney(player) + amount);
    }

    public static void setMoney(Player player, int amount) {
        getConfig().set(player.getUniqueId().toString() + ".money", amount);
        yamlFile.save();
    }

    public static int getMoney(Player player) {
        return getConfig().getInt(player.getUniqueId().toString() + ".money");
    }

    public static List<String> getOwnedKits(Player player) {
        List<String> kits = getConfig().getStringList(player.getUniqueId().toString() + ".kits");
        kits.addAll(Kit.getFreeKits());
        return kits;
    }

    public static void addOwnedKits(Player player, String kitName) {
        List<String> kits = getConfig().getStringList(player.getUniqueId().toString() + ".kits");
        kits.add(kitName);
        getConfig().set(player.getUniqueId().toString() + ".kits", kits);
        yamlFile.save();
    }

}
