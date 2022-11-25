package dev.micah.arun.utils;

import dev.micah.arun.reflection.NMSClassHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.Field;

public class InventoryTitleReflection {

    public static String getTitle(InventoryClickEvent inventory) {
        if (NMSClassHandler.containsVersion("1.13", "1.14", "1.15", "1.16")) {
            try {
                Field field = inventory.getClass().getField("getView");
                field.setAccessible(true);
                InventoryView inventoryView = (InventoryView) field.get(null);
                return inventoryView.getTitle();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return inventory.getInventory().getTitle();
    }

}
