package dev.micah.arun.kits;

import dev.micah.arun.Arun;
import dev.micah.arun.io.YamlFile;
import dev.micah.arun.utils.ItemStackUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final static YamlConfiguration config = Arun.getKitManager().getYamlFile().getConfig();
    private final YamlFile yamlFile = Arun.getKitManager().getYamlFile();

    private ItemStack[] items;
    private String name;

    public static List<String> getKits() {
        return config.getStringList("kits-list");
    }

    public static List<String> getFreeKits() {
        List<String> result = new ArrayList<>();
        getKits().forEach(s -> {
            Kit kit = new Kit(s);
            if (kit.getItems() != null) {
                if (kit.getCost() == 0) {
                    result.add(kit.getName());
                }
            }
        });
        return result;
    }

    /**
     *
     * Use this when creating a kit
     *
     * @param name
     * @param items
     */
    public Kit(String name, ItemStack[] items) {
        this.items = items;
        this.name = name;
        List<String> kits = config.getStringList("kits-list");
        if (!kits.contains(name)) {
            kits.add(name);
            config.set("kits-list", kits);
            yamlFile.save();
        }
    }

    /**
     *
     * Use this when getting a kit
     *
     * @param name
     */
    public Kit(String name) {
        this.name = name;
    }

    public void saveItemsToConfig() {
        List<String> itemStackList = config.getStringList("kits." + name + ".items");
        if (itemStackList == null) itemStackList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null) {
                if (!itemStackList.contains(ItemStackUtil.toString(item))) {
                    itemStackList.add(ItemStackUtil.toString(item));
                }
            }
        }
        config.set("kits." + name + ".items", itemStackList);
        yamlFile.save();
    }

    public void setPrice(int price) {
        config.set("kits." + name + ".price", price);
        yamlFile.save();
    }

    public int getCost() {
        return config.getInt("kits." + name + ".price");
    }

    public void delete() {
        List<String> kits = config.getStringList("kits-list");
        if (kits.contains(name)) {
            kits.remove(name);
            config.set("kits-list", kits);
        }
        config.set("kits." + name, null);
        yamlFile.save();
    }

    public List<ItemStack> getItems() {
        List<String> itemStackList = config.getStringList("kits." + name + ".items");
        List<ItemStack> itemsList = new ArrayList<>();
        for (String s : itemStackList) {
            itemsList.add(ItemStackUtil.fromString(s));
        }
        return itemsList;
    }

    public String getName() {
        return name;
    }

}
