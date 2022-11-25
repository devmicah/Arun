package dev.micah.arun.kits.gui;

import dev.micah.arun.Arun;
import dev.micah.arun.economy.EconomyManager;
import dev.micah.arun.game.Game;
import dev.micah.arun.game.GameInfo;
import dev.micah.arun.game.GameManager;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.kits.Kit;
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

import java.util.ArrayList;
import java.util.List;

public class GuiKitSelector implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        Inventory gui = Bukkit.createInventory(null, 3*9, Chat.color("&cKit Selector"));
        getKitItems(player).forEach(itemStack -> gui.setItem(gui.firstEmpty(), itemStack));
        player.openInventory(gui);
    }

    public List<ItemStack> getKitItems(Player player) {
        List<ItemStack> result = new ArrayList<>();
        result.add(ItemBuilder.createItem(Material.WOOD_PICKAXE, "&7Default Kit"));
        for (String kitName : EconomyManager.getOwnedKits(player)) {
            Kit kit = new Kit(kitName);
            if (kit.getItems() != null) {
                result.add(ItemBuilder.createItem(kit.getItems().get(0).getType(), "&c" + kit.getName()));
            }
        }
        return result;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Kit Selector")) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                if (item.getItemMeta() != null) {
                    Game game = Arun.getGameManager().getPlayersGame(player);
                    if (game != null) {
                        game.getSelectedKit().put(player, ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        player.sendMessage(Chat.color("&c[Arun] &7You selected &c&l" + ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                        player.closeInventory();
                    }
                }
                event.setCancelled(true);
            }
        }
    }

}
