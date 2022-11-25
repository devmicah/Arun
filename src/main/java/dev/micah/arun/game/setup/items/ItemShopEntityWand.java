package dev.micah.arun.game.setup.items;

import dev.micah.arun.Arun;
import dev.micah.arun.game.Game;
import dev.micah.arun.reflection.NMSClassHandler;
import dev.micah.arun.shop.GuiShop;
import dev.micah.arun.shop.ShopEntity;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ItemShopEntityWand implements Listener {

    public ItemStack getItem() {
        return ItemBuilder.createItem(Material.ARROW, "&cKill Shop Wand", " ",
                "&cRight Click &7&lTurn into Shop");
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            if (event.getRightClicked().getCustomName() != null) {
                if (event.getRightClicked().getCustomName().contains("Kill Points Shop")) {
                    Game game = Arun.getGameManager().getPlayersGame(event.getPlayer());
                    if (game != null) {
                        new GuiShop().open(event.getPlayer(), new Object[]{game});
                    } else {
                        event.getPlayer().sendMessage(Chat.color("&cYou must be in a game to open kill point shops!"));
                    }
                    event.setCancelled(true);
                }
            }
            if (event.getPlayer().getItemInHand().getType() == Material.ARROW && event.getPlayer().getItemInHand().getItemMeta() != null) {
                event.setCancelled(true);
                if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Kill Shop Wand")) {
                    new ShopEntity((Villager) event.getRightClicked());
                }
            }
        }
    }

}
