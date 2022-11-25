package dev.micah.arun.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {

    // Example String: STONE;7;startEnchants,unbreaking$3,endEnchants;&7Item Name
    public static String toString(ItemStack itemStack) {
        String finalString = itemStack.getType().name().toUpperCase() + ";" + itemStack.getAmount() + ";startEnchants";
        if (!itemStack.getEnchantments().isEmpty()) {
            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                int level = itemStack.getEnchantmentLevel(enchantment);
                finalString += "," + enchantment.getName().toLowerCase() + "%" + level;
            }
            finalString += ",endEnchants;";
        } else {
            finalString += ",endEnchants;";
        }
        if (itemStack.getItemMeta() != null) {
            if (itemStack.getItemMeta().getDisplayName() != null) {
                finalString += itemStack.getItemMeta().getDisplayName();
            }
        }
        return finalString;
    }

    public static ItemStack fromString(String string) {
        String[] values = string.split(";");
        Material material = Material.valueOf(values[0]);
        int amount = Integer.parseInt(values[1]);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        String[] enchantValues = values[2].split(",");
        for (String s : enchantValues) {
            if (s.contains("startEnchants")) continue;
            if (s.contains("endEnchants")) break;
            String enchantName = s.split("%")[0];
            int level = Integer.valueOf(s.split("%")[1]);
            item.addEnchantment(Enchantment.getByName(enchantName.toUpperCase()), level);
        }
        if (values.length >= 4) {
            if (values[3] != null) {
                meta.setDisplayName(Chat.color(values[3]));
                item.setItemMeta(meta);
            }
        }
        return item;
    }

}
