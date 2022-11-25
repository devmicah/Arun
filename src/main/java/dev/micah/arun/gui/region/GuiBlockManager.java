package dev.micah.arun.gui.region;

import dev.micah.arun.Arun;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiBlockManager implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9*4, Chat.color("&cBlock Manager"));

        int[] fillSlots = new int[] {0,1,2,3,4,5,6,7};
        for (int i : fillSlots) { gui.setItem(i, ItemBuilder.createItem(Material.IRON_BLOCK, "&cAdd/Remove &7Blocks Below")); }

        gui.setItem(8, ItemBuilder.createItem(Material.BOOK, "&c" + gameName,
                " ", "&7You are currently", "&7editing this game."));

        for (Material m : Arun.getRegionAPI().getBlockTypes(gameName)) {
            gui.setItem(gui.firstEmpty(), new ItemStack(m));
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(Chat.color("&cBlock Manager"))) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
            if (item != null) {
                if (item.getType() == Material.IRON_BLOCK && item.getItemMeta() != null) {
                    event.setCancelled(true);
                }
                if (item.getType() == Material.BARRIER) {
                    new GuiRegionManager().open(player, new Object[]{gameName});
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(Chat.color("&cBlock Manager"))) {
            String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
            Arun.getRegionAPI().clearBlockTypes(gameName);
            List<ItemStack> items = new ArrayList<>();
            for (int i = 9; i < 35; i++) {
                if (event.getInventory() != null) {
                    if (event.getInventory().getItem(i) != null) {
                        items.add(event.getInventory().getItem(i));
                    }
                }
            }
            for (ItemStack i : items) {
                Arun.getRegionAPI().addBlockTypes(gameName, i.getType());
            }
        }
    }

}
