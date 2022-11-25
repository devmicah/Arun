package dev.micah.arun.gui.region;

import dev.micah.arun.Arun;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.Cuboid2D;
import dev.micah.arun.utils.ItemBuilder;
import dev.micah.arun.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiRegionEditor implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9*4, Chat.color("&cRegion Manager"));

        int[] fillSlots = new int[] {0,1,2,3,4,5,6,7};
        for (int i : fillSlots) { gui.setItem(i, ItemBuilder.createItem(Material.IRON_BLOCK, "&cEdit Regions Below")); }

        gui.setItem(8, ItemBuilder.createItem(Material.BOOK, "&c" + gameName,
                " ", "&7You are currently", "&7editing this game."));

        int index = 0;
        for (Cuboid2D cuboid2D : Arun.getRegionAPI().getRegions(gameName)) {
            gui.setItem(gui.firstEmpty(), ItemBuilder.createItem(Material.GLASS, "&cRegion #" + (index + 1), " ",
                    "&cPostion 1: &7" + LocationUtil.asDisplayString(cuboid2D.getLoc1()),
                    "&cPostion 2: &7" + LocationUtil.asDisplayString(cuboid2D.getLoc2()),
                    " ",
                    "&cShift + Click &7to delete"));
            index++;
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isShiftClick() && event.getView().getTitle().contains("Region Manager")) {
            String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getItemMeta() != null) {
                    int index = Integer.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().split("n")[1].substring(1).split(":")[0]).replaceAll("#", ""));
                    Arun.getRegionAPI().removeRegion(gameName, index-1);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(Chat.color("&c[Arun] &7Region removed from game &c" + gameName + "&7!"));
                }
            }
            event.setCancelled(true);
        }
    }

}
