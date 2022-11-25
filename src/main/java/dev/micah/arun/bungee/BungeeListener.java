package dev.micah.arun.bungee;

import dev.micah.arun.Arun;
import dev.micah.arun.game.Game;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BungeeListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Game game = Arun.getGameManager().getGames().get(0);
        Player player = event.getPlayer();
        if ((Boolean) ConfigUtil.getConfigValue("bungee")) {
            if (game == null) {
                player.sendMessage(Chat.color("&c[Arun] &7This server has no been setup please contact an admin!"));
                return;
            }
            game.addPlayer(player);
            event.setJoinMessage(null);
        }
    }

}
