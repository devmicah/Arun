package dev.micah.arun.gui.game;

import dev.micah.arun.Arun;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import dev.micah.arun.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiCoreBlockManager implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9*3, Chat.color("&cCore Block Manager"));

        int[] fillSlots = new int[] {0,1,2,3,4,5,6,7};
        for (int i : fillSlots) { gui.setItem(i, ItemBuilder.createItem(Material.IRON_BLOCK, "&cEdit Core Blocks Below")); }

        gui.setItem(8, ItemBuilder.createItem(Material.BOOK, "&c" + gameName,
                " ", "&7You are currently", "&7editing this game."));

        int index = 0;
        for (String team : Arun.getGameAPI().getTeamsWithCoreBlocks(gameName)) {
            Location location = Arun.getGameAPI().getCoreBlockLocation(gameName, team);
            gui.setItem(gui.firstEmpty(), ItemBuilder.createItem(Material.OBSIDIAN, "&cCore Block Location #" + (index + 1), " ",
                    "&cTeam: &f" + team.toUpperCase(), "&cLocation: &f" + LocationUtil.asDisplayString(location), " ",
                    "&cShift + Click &7to delete."));
            index++;
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Core Block Manager")) {
            Player p = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null) {
                String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
                if (event.isShiftClick()) {
                    if (event.getCurrentItem().getItemMeta() != null) {
                        String team = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1).split(":")[1].substring(1).toUpperCase());
                        Arun.getGameAPI().deleteCoreBlockLocation(gameName, team);
                        p.sendMessage(Chat.color("&c[Arun] You removed the core block location for team: &f" + team));
                        p.closeInventory();
                    }
                }
                event.setCancelled(true);
            }
        }
    }

}
