package dev.micah.arun.gui.game;

import dev.micah.arun.Arun;
import dev.micah.arun.game.setup.items.ItemCoreBlockWand;
import dev.micah.arun.gui.Gui;
import dev.micah.arun.team.TeamMode;
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

public class GuiGameManager implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        String gameName = (String) input[0];

        Inventory gui = Bukkit.createInventory(null, 9, Chat.color("&cGame Manager"));

        TeamMode mode = Arun.getGameAPI().getTeamMode(gameName);
        boolean twoPlatforms = Arun.getGameAPI().isTwoPlatforms(gameName);

        gui.setItem(2, ItemBuilder.createItem(Material.LEATHER_HELMET, "&cTeam Mode",
                " ", "&7Choose a team mode from either", "&71v1, 2v2, 3v3, or 4v4.",
                " ",
                "&cCurrently Selected: &f" + mode.getDisplayString()));

        gui.setItem(1, ItemBuilder.createItem(Material.BEACON, "&cPlatforms Amount",
                " ", "&7Choose the amount of platforms. You", "&7can choose either 2 or 4.", "&7Your selected amount will effect", "&7the team mode (ex. 2v2v2v2).",
                " ",
                "&cCurrently Selected: " + (twoPlatforms ? "&f2 Platforms" : "&f4 Platforms")));

        gui.setItem(0, ItemBuilder.createItem(Material.STICK, "&cCore Block Wand",
                " ", "&7Use this wand to click", "&7on the in game core blocks. Make", "&7sure you reach the required", "&7amount."));

        gui.setItem(3, ItemBuilder.createItem(Material.OBSIDIAN, "&cCore Block Manager",
                " ", "&7Manage all the core blocks", "&7set for this game. View/Delete", "&7them."));

        gui.setItem(8, ItemBuilder.createItem(Material.BOOK, "&c" + gameName,
                " ", "&7You are currently", "&7editing this game."));

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Game Manager")) {
            String gameName = ChatColor.stripColor(event.getInventory().getItem(8).getItemMeta().getDisplayName());
            Player p = (Player) event.getWhoClicked();
            Inventory gui = event.getInventory();
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                if (item.getType() == Material.LEATHER_HELMET) {
                    TeamMode mode = next(Arun.getGameAPI().getTeamMode(gameName));
                    gui.setItem(2, ItemBuilder.createItem(Material.LEATHER_HELMET, "&cTeam Mode",
                            " ", "&7Choose a team mode from either", "&71v1, 2v2, 3v3, or 4v4.",
                            " ",
                            "&cCurrently Selected: &f" + mode.getDisplayString()));
                    Arun.getGameAPI().setTeamMode(gameName, mode);
                    p.updateInventory();
                }
                if (item.getType() == Material.BEACON) {
                    boolean twoPlatforms = Arun.getGameAPI().isTwoPlatforms(gameName);
                    gui.setItem(1, ItemBuilder.createItem(Material.BEACON, "&cPlatforms Amount",
                            " ", "&7Choose the amount of platforms. You", "&7can choose either 2 or 4.", "&7Your selected amount will effect", "&7the team mode (ex. 2v2v2v2).",
                            " ",
                            "&cCurrently Selected: " + (!(twoPlatforms) ? "&f2 Platforms" : "&f4 Platforms")));
                    Arun.getGameAPI().setTwoPlatforms(gameName, !twoPlatforms);
                    p.updateInventory();
                }
                if (item.getType() == Material.STICK) {
                    p.closeInventory();
                    p.sendMessage(Chat.color("&c[Arun] &7Click on any block that will be a core block. Then select a team for it."));
                    ItemCoreBlockWand itemCoreBlockWand = new ItemCoreBlockWand();
                    p.getInventory().addItem(itemCoreBlockWand.getItem(gameName));
                }
                if (item.getType() == Material.OBSIDIAN) {
                    new GuiCoreBlockManager().open(p, new Object[]{gameName});
                }
                event.setCancelled(true);
            }
        }
    }

    private TeamMode next(TeamMode teamMode) {
        TeamMode[] teamModes = TeamMode.values();
        for (int i = 0; i < teamModes.length; i++) {
            if (teamModes[i] == teamMode) {
                if (teamMode == TeamMode.SQUAD) return TeamMode.SOLO;
                return teamModes[i + 1];
            }
        }
        return null;
    }

}
