package dev.micah.arun.kits.gui;

import dev.micah.arun.economy.EconomyManager;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiKitShop implements Gui, Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Kit Shop")) {
            Player player = (Player) event.getWhoClicked();
            int page = Integer.parseInt(event.getView().getTitle().split(":")[1].replaceAll(" ", ""));
            int coins = EconomyManager.getMoney(player);
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getItemMeta() == null) return;
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (item.getType() == Material.ARROW && item.getItemMeta().getDisplayName().contains("Next")) {
                    createKitInventory(player, page + 1);
                    return;
                }
                if (item.getType() == Material.ARROW && item.getItemMeta().getDisplayName().contains("Previous")) {
                    createKitInventory(player, page - 1);
                    return;
                }
                Kit kit = new Kit(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if (kit.getItems() != null) {
                    if (kit.getCost() <= coins) {
                        if (item.getItemMeta().getLore().get(2).contains("already")) {
                            player.closeInventory();
                            player.sendMessage(Chat.color("&c[Arun] &7You already own this kit!"));
                            return;
                        }
                        EconomyManager.setMoney(player, coins - kit.getCost());
                        EconomyManager.addOwnedKits(player, kit.getName());
                        player.closeInventory();
                        player.sendMessage(Chat.color("&c[Arun] &7You purchase the kit &c" + kit.getName() + " &7for &6$" + kit.getCost() + "&7!"));
                    } else {
                        player.closeInventory();
                        player.sendMessage(Chat.color("&c[Arun] &7You do not have enough coins to purchase this!"));
                    }
                }
            }
        }
    }

    @Override
    public void open(Player player, Object[] input) {
        createKitInventory(player, 1);
    }

    private void createKitInventory(Player player, int page) {
        Inventory gui = Bukkit.createInventory(null, 9 * 4, Chat.color("&cKit Shop - Page: " + page));
        int[] filler = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
        for (int slot : filler) {
            gui.setItem(slot, ItemBuilder.createItem(Material.IRON_BLOCK, "&c "));
        }
        List<Kit> kits = new ArrayList<>();
        Kit.getKits().forEach(s -> kits.add(new Kit(s)));
        getPageItems(kits, page, player).forEach(itemStack -> gui.setItem(gui.firstEmpty(), itemStack));
        if (isPageValid(kits, page + 1, player)) gui.setItem(35, ItemBuilder.createItem(Material.ARROW, "&aNext Page"));
        if (isPageValid(kits, page - 1, player))
            gui.setItem(27, ItemBuilder.createItem(Material.ARROW, "&aPrevious Page"));
        player.openInventory(gui);
    }

    private boolean isPageValid(List<Kit> kits, int page, Player player) {
        List<ItemStack> kitItems = new ArrayList<>();
        kits.forEach(kit -> kitItems.add(createKitItem(player, kit)));
        int spaces = 14;
        if (page <= 0) return false;
        int upper = page * spaces;
        int lower = upper - spaces;
        return kitItems.size() > lower;
    }

    private List<ItemStack> getPageItems(List<Kit> kits, int page, Player player) {
        List<ItemStack> kitItems = new ArrayList<>();
        kits.forEach(kit -> kitItems.add(createKitItem(player, kit)));
        if (kitItems.size() <= 14) {
            return kitItems;
        }
        int spaces = 14;
        int upper = page * spaces;
        int lower = upper - spaces;
        List<ItemStack> newList = new ArrayList<>();
        for (int i = lower; i < upper; i++) {
            if (i >= kitItems.size()) {
                return newList;
            }
            newList.add(kitItems.get(i));
        }
        return newList;
    }

    private ItemStack createKitItem(Player player, Kit kit) {
        boolean owns = EconomyManager.getOwnedKits(player).contains(kit.getName());
        ItemStack itemStack = new ItemStack(kit.getItems().get(0));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Chat.color("&c" + kit.getName()));
        itemMeta.setLore(Arrays.asList(
                " ",
                Chat.color("&7Cost: " + (kit.getCost() == 0 ? "&aFREE" : "&6" + kit.getCost())),
                Chat.color(owns ? "&aYou already own this kit" : "&cYou do not own this kit")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
