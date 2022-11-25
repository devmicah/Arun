package dev.micah.arun.kits.gui;

import dev.micah.arun.gui.Gui;
import dev.micah.arun.kits.Kit;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiViewKit implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        Kit kit = (Kit) input[0];
        Inventory gui = Bukkit.createInventory(null, 9*4, Chat.color("&cViewing Kit: &7" + kit.getName()));
        gui.setItem(0, ItemBuilder.createItem(Material.BOOK, "&c" + kit.getName(), " ", "&6Cost: &7" + (kit.getCost() > 0 ? kit.getCost() : "Free")));
        for (int i = 0; i < 8; i++) {
            gui.setItem(i + 1, ItemBuilder.createItem(Material.IRON_BLOCK, "&7KIT ITEMS BELOW"));
        }
        for (ItemStack item : kit.getItems()) {
            gui.setItem(gui.firstEmpty(), item);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Viewing Kit:")) {
            event.setCancelled(true);
        }
    }

}
