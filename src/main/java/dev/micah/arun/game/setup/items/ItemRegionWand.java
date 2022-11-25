package dev.micah.arun.game.setup.items;

import dev.micah.arun.Arun;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemRegionWand implements Listener {

    public ItemStack getItem(String gameName) {
        return ItemBuilder.createItem(Material.BLAZE_ROD, "&cRegion Wand - " + gameName, " ",
                "&cLeft Click &7&lPosition 1", "&cRight Click &7&lPosition 2", "&cShift + Click &7&lCreate Region");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getItemMeta() != null) {
            if (event.getItem().getType() == Material.BLAZE_ROD && event.getItem().getItemMeta().getDisplayName().contains("Wand")) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    point2 = event.getClickedBlock().getLocation();
                    event.getPlayer().sendMessage(Chat.color("&c[Arun] &7You set the location of &cPosition 2"));
                }
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    point1 = event.getClickedBlock().getLocation();
                    event.getPlayer().sendMessage(Chat.color("&c[Arun] &7You set the location of &cPosition 1"));
                }
                String gameName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().split("-")[1].substring(1));
                if (event.getPlayer().isSneaking()) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
                        if (point1 != null && point2 != null) {
                            if (point1.getY() != point2.getY()) {
                                event.getPlayer().sendMessage(Chat.color("&c[Arun] &7The region must have the same Y level"));
                            }
                            if (!Arun.getRegionAPI().containsRegion(gameName, point1, point2)) {
                                Arun.getRegionAPI().addRegion(gameName, point1, point2);
                                event.getPlayer().sendMessage(Chat.color("&c[Arun] &7You saved a region to config!"));
                            } else {
                                event.getPlayer().sendMessage(Chat.color("&c[Arun] &7A region with these positions already exists!"));
                            }
                        } else {
                            event.getPlayer().sendMessage(Chat.color("&c[Arun] &7You cannot save a region because 2 points haven't been selected!"));
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    public static Location point1;
    public static Location point2;

}
