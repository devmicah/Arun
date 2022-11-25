package dev.micah.arun.game;

import dev.micah.arun.Arun;
import dev.micah.arun.economy.EconomyManager;
import dev.micah.arun.kits.gui.GuiKitSelector;
import dev.micah.arun.team.Team;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ConfigUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Game game = Arun.getGameManager().getPlayersGame(event.getPlayer());
        if (game != null) {
            game.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.VILLAGER) {
            if (event.getEntity().getCustomName().contains("Kill Points Shop")) {
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (Arun.getGameManager().getPlayersGame(player) != null) {
                Game game = Arun.getGameManager().getPlayersGame(player);
                if (game.getGameState() == GameState.LOBBY) {
                    event.setCancelled(true);
                    System.out.println("test");
                    return;
                }
                game.getLastHitPlayer().put(player, damager);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Arun.getGameManager().getPlayersGame(event.getPlayer()) != null) {
            Player player = event.getPlayer();
            Game game = Arun.getGameManager().getPlayersGame(event.getPlayer());
            player.setFoodLevel(20);
            if (game.getMainYLevel() != 0) {
                if (player.getLocation().getY() < game.getMainYLevel() - 50) {
                    player.setHealth(0);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (Arun.getGameManager().getPlayersGame(p) != null) {
            Game game = Arun.getGameManager().getPlayersGame(p);
            if (game.getTeams() == null) {
                event.setCancelled(true);
                return;
            }
            for (Team team : game.getTeams()) {
                Location coreBlock = Arun.getGameAPI().getCoreBlockLocation(game.getGameInfo().getGameName(), team.getTeamName());
                if (event.getBlock().getLocation().equals(coreBlock)) {
                    if (team.getTeamPlayers().contains(event.getPlayer())) {
                        event.getPlayer().sendMessage(Chat.color("&c[Arun] &7You cannot break your own core block!"));
                        event.setCancelled(true);
                        return;
                    }
                    int coinAdd = (Integer) ConfigUtil.getConfigValue("game-rewards.core-block-break");
                    if (coinAdd != 0) {
                        EconomyManager.addMoney(p, coinAdd);
                        p.sendMessage(Chat.color("&6+" + coinAdd + " coin" + (coinAdd == 1 ? "" : "s") + " for breaking a core block!"));
                    }
                    game.getTeamsWithoutCoreBlocks().add(team);
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                    for (Player all : game.getGameInfo().getPlayers()) {
                        all.sendMessage(" ");
                        all.sendMessage(Chat.color(Chat.getColorFromName(team.getTeamName()) + team.getTeamName() + " &7have lost their core block and will no longer respawn!"));
                        all.sendMessage(" ");
                    }
                    return;
                }
            }
            p.sendMessage(Chat.color("&cYou cannot break blocks!"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (Arun.getGameManager().getPlayersGame(event.getEntity()) != null) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            Player player = event.getEntity();
            Game game = Arun.getGameManager().getPlayersGame(event.getEntity());
            doDeath(player, game);
        }
    }

    private void doDeath(Player player, Game game) {
        Player killer = null;
        String killMessage = Chat.color("&c" + player.getName() + " &7has died!");
        if (player.getKiller() != null) {
            killer = player.getKiller();
        }
        if (game.getLastHitPlayer().containsKey(player)) {
            killer = game.getLastHitPlayer().get(player);
        }
        if (killer != null) {
            game.getKillPoints().put(killer, game.getKillPoints().getOrDefault(killer, 0) + 1);
            killer.sendMessage(Chat.color("&c&l[+] &r&7You gained a &8Kill Point&7!"));
            killMessage = Chat.color("&c" + player.getName() + "&7 was killed by &c" + killer.getName() + "&7!");
            int coinAdd = (Integer) ConfigUtil.getConfigValue("game-rewards.player-kill");
            if (coinAdd != 0) {
                EconomyManager.addMoney(killer, coinAdd);
                killer.sendMessage(Chat.color("&6+" + coinAdd + " coin" + (coinAdd == 1 ? "" : "s") + " for getting a kill!"));
            }
            game.getLastHitPlayer().remove(player);
        }
        player.spigot().respawn();
        Team team = game.getPlayersTeam(player);
        if (team != null) {
            if (game.getTeamsWithoutCoreBlocks().contains(team)) {
                player.sendMessage(Chat.color("&c[Arun] &7You died and won't respawn because you do not have a core block!"));
                team.getTeamPlayers().remove(player);
                if (team.getTeamPlayers().size() == 0) {
                    game.getEliminatedTeams().add(team);
                    game.getTeams().remove(team);
                    for (Player all : game.getGameInfo().getPlayers()) {
                        all.sendMessage(" ");
                        all.sendMessage(Chat.color(Chat.getColorFromName(team.getTeamName()) + team.getTeamName() + " &7have been eliminated from the game!"));
                        all.sendMessage(" ");
                    }
                }
                if (game.getTeams().size() == 1) {
                    game.endGame(game.getTeams().get(0));
                }
                player.teleport(Arun.getGameManager().getLastLocation().getOrDefault(player,
                        Arun.getGameAPI().getSpawnLocation(game.getGameInfo().getGameName(), team.getTeamName())));
                return;
            }
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getGameInfo().getLobbyLocation());
        String finalKillMessage = killMessage;
        game.getGameInfo().getPlayers().forEach(p -> p.sendMessage(finalKillMessage));
        startRespawnTimer(player, game);
    }

    private void startRespawnTimer(Player player, Game game) {
        new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                if (seconds == 0) {
                    game.getTeams().forEach(t -> {
                        if (t.getTeamPlayers().contains(player)) {
                            player.setFoodLevel(20);
                            player.setHealth(20.0);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleport(Arun.getGameAPI().getSpawnLocation(game.getGameInfo().getGameName(), t.getTeamName()));
                            player.sendMessage(Chat.color("&cYou have been respawned!"));
                            game.giveItems(player);
                        }
                    });
                    cancel();
                }
                player.sendMessage(Chat.color("&c&lYou will respawn in " + seconds + " second" + (seconds == 1 ? "" : "s")));
                seconds--;
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Arun.class), 20L, 20L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType().name().contains("BED")) {
                if (event.getItem().getItemMeta() != null) {
                    if (event.getItem().getItemMeta().getDisplayName().contains("Leave")) {
                        Game game = Arun.getGameManager().getPlayersGame(event.getPlayer());
                        if (game != null) {
                            game.removePlayer(event.getPlayer());
                            event.getPlayer().getInventory().clear();
                        }
                    }
                }
            }
            if (event.getItem().getType() == Material.BOW) {
                if (event.getItem().getItemMeta() != null) {
                    if (event.getItem().getItemMeta().getDisplayName().contains("Kit")) {
                        new GuiKitSelector().open(event.getPlayer(), null);
                    }
                }
            }
        }

    }

}
