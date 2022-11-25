package dev.micah.arun.gui.join;

import dev.micah.arun.Arun;
import dev.micah.arun.game.Game;
import dev.micah.arun.game.GameInfo;
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

public class GuiGameJoin implements Gui, Listener {

    @Override
    public void open(Player player, Object[] input) {
        Inventory gui = Bukkit.createInventory(null, 9*3, Chat.color("&cArun - Games"));
        for (String gameName : Arun.getGameAPI().getPublishedGames()) {
            if (!Arun.getGameManager().isGameReady(gameName)) {
                return;
            }
            Game game = Arun.getGameManager().getGameByName(gameName);
            if (game == null) continue;
            GameInfo gameInfo = Arun.getGameManager().getGameInfo(gameName);
            Material material = Material.EMERALD_BLOCK;
            String addOn = "";
            if (game.getGameInfo().getPlayers().size() == gameInfo.getMaxPlayers()) {
                material = Material.REDSTONE_BLOCK;
                addOn = " - Game Started";
            }
            gui.setItem(gui.firstEmpty(), ItemBuilder.createItem(material, "&c" + gameName + addOn, " ",
                    "&7Click to join this game!", " ",
                    "&cMode: &f" + gameInfo.getTeamMode().getDisplayString(),
                    "&cPlatforms: &f" + (gameInfo.isTwoPlatforms() ? "2" : "4"),
                    "&cPlayers: &f" + game.getGameInfo().getPlayers().size() + "&c/&f" + gameInfo.getMaxPlayers()));
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Arun - Games")) {
            Player p = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null) {
                String gameName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                Game game = Arun.getGameManager().getGameByName(gameName);
                game.addPlayer(p);
            }
        }
    }

}
