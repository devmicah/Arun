package dev.micah.arun.gui.publish;

import dev.micah.arun.Arun;
import dev.micah.arun.game.setup.items.ItemSpawnLocationButton;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.utils.AttachedBoolean;
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

public class GuiPublishManager implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9, Chat.color("&cPublish Manager - " + gameName));

        gui.setItem(8, buildRemainingTasksItem(gameName, gui));
        gui.setItem(0, ItemBuilder.createItem(Material.ENDER_PEARL, "&aSet Lobby", " ", "&7Click to set the", "&7lobby for this game."));
        gui.setItem(1, ItemBuilder.createItem(Material.STONE_BUTTON, "&aSpawn Location Button", " ",
                "&7Left Click with this in",
                "&7your hand to be prompted",
                "&7on the lobby locations.", " ",
                "&7Shift + Left Click to delete",
                "&7a spawn location."));

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Publish Manager")) {
            Player p = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            String gameName = ChatColor.stripColor(event.getView().getTitle().split("-")[1].substring(1));
            if (item != null) {
                if (item.getType() == Material.STONE_BUTTON) {
                    p.closeInventory();
                    p.sendMessage(Chat.color("&c[Arun] &7While holding this button &cLeft Click &7to set a spawn location. &cShift + Left Click &7to delete a spawn."));
                    p.getInventory().addItem(new ItemSpawnLocationButton().getItem(gameName));
                }
                if (item.getType() == Material.ENDER_PEARL) {
                    p.closeInventory();
                    p.sendMessage(Chat.color("&c[Arun] &7Lobby for game &c" + gameName + " &7has been set!"));
                    Arun.getGameAPI().setLobbySpawn(gameName, p.getLocation());
                }
                if (item.getType() == Material.EMERALD_BLOCK) {
                    p.closeInventory();
                    p.sendMessage(Chat.color("&c[Arun] " + gameName + " &7has been published! Use &c/arun join &7to see it!"));
                    Arun.getGameAPI().publish(gameName);
                }
                event.setCancelled(true);
            }
        }
    }

    public ItemStack buildRemainingTasksItem(String gameName, Inventory gui) {
        AttachedBoolean coreBlocks = new AttachedBoolean(Arun.getGameAPI().getTeamsWithCoreBlocks(gameName).size() ==
                (Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4), "&fSet Core Block Locations");
        AttachedBoolean region = new AttachedBoolean(Arun.getRegionAPI().getRegions(gameName).size() > 0, "&fSet at least 1 Region");
        AttachedBoolean regionBlocks = new AttachedBoolean(Arun.getRegionAPI().getBlockTypes(gameName).size() > 0, "&fSet at least 1 Region Block");
        AttachedBoolean spawnLocations = new AttachedBoolean(Arun.getGameAPI().getSpawnLocations(gameName).size() ==
                (Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4), "&fSet the needed amount of spawns");
        AttachedBoolean lobbyLocation = new AttachedBoolean(Arun.getGameAPI().getLobbySpawn(gameName) != null, "&fSet a Lobby Location");

        List<AttachedBoolean> all = Arrays.asList(coreBlocks, region, regionBlocks, spawnLocations, lobbyLocation);
        List<AttachedBoolean> done = new ArrayList<>();
        List<AttachedBoolean> need = new ArrayList<>();

        all.forEach(attachedBoolean -> {
            if (attachedBoolean.getBoolean()) {
                done.add(attachedBoolean);
            } else {
                need.add(attachedBoolean);
            }
        });

        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        lore.add(Chat.color("&aFinished Tasks:"));
        for (AttachedBoolean attachedBoolean : done) {
            lore.add(Chat.color("&7- &r" + attachedBoolean.getValue()));
        }
        if (need.size() > 0) {
            lore.add(Chat.color(" "));
            lore.add(Chat.color("&cNeeded Tasks:"));
            for (AttachedBoolean attachedBoolean : need) {
                lore.add(Chat.color("&7- &r" + attachedBoolean.getValue()));
            }
        } else {
            lore.add(Chat.color(" "));
            lore.add(Chat.color("&a&lAll tasks finished!"));
            gui.setItem(7, ItemBuilder.createItem(Material.EMERALD_BLOCK, "&aPublish Game", " ",
                    "&7Everything is ready to go!", "&7Click me to publish this game", "&7and allow players to join!"));
        }
        meta.setLore(lore);
        meta.setDisplayName(Chat.color("&cGame Status"));
        item.setItemMeta(meta);
        return item;
    }

}
