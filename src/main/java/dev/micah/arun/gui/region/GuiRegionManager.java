package dev.micah.arun.gui.region;

import dev.micah.arun.game.setup.items.ItemRegionWand;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiRegionManager implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9, Chat.color("&cRegion Manager"));
        
        gui.setItem(0, ItemBuilder.createItem(Material.BLAZE_ROD, "&cRegion Wand",
                " ", "&7Select/Create regions where", "&7parkour will spawn during the game.", " ", "&cLeft Click &7- &fPosition 1", "&cRight Click &7- &fPosition 2"));
        gui.setItem(1, ItemBuilder.createItem(Material.STONE, "&cBlock Manager",
                " ", "&7Choose the block types that", "&7will generate during the game."));
        gui.setItem(2, ItemBuilder.createItem(Material.ANVIL, "&cRegion Manager",
                " ", "&7Manage all the regions for", "&7this current game."));
        gui.setItem(8, ItemBuilder.createItem(Material.BOOK, "&c" + gameName,
                " ", "&7You are currently", "&7editing this game."));

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Region Manager")) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
            if (item != null) {
                if (item.getType() == Material.BLAZE_ROD) {
                    ItemRegionWand itemRegionWand = new ItemRegionWand();
                    player.getInventory().addItem(itemRegionWand.getItem(gameName));
                    player.sendMessage(Chat.color("&c[Arun] &7Hover over the wand in your inventory for more details!"));
                    player.closeInventory();
                }
                if (item.getType() == Material.STONE) {
                    new GuiBlockManager().open(player, new Object[]{gameName});
                }
                if (item.getType() == Material.ANVIL) {
                    new GuiRegionEditor().open(player, new Object[]{gameName});
                }
            }
            event.setCancelled(true);
        }
    }
}
