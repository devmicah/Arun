package dev.micah.arun.game;

import dev.micah.arun.Arun;
import dev.micah.arun.economy.EconomyManager;
import dev.micah.arun.generator.ParkourGenerator;
import dev.micah.arun.kits.Kit;
import dev.micah.arun.shop.GuiShop;
import dev.micah.arun.team.Team;
import dev.micah.arun.team.TeamManager;
import dev.micah.arun.utils.Chat;
import dev.micah.arun.utils.ConfigUtil;
import dev.micah.arun.utils.Cuboid2D;
import dev.micah.arun.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Game {

    private GameInfo gameInfo;
    private TeamManager teamManager;
    private GameManager gameManager;

    private List<Team> teams;

    private HashMap<Player, String> selectedKit;
    private HashMap<Player, Integer> killPoints;
    private HashMap<Player, Player> lastHitPlayer;
    private HashMap<Player, GuiShop.PickaxeUpgrade> pickaxeUpgrade;
    private HashMap<String, GuiShop.CoreBlockUpgrade> coreBlockUpgrade;
    private List<Team> noCoreBlocks;
    private List<Team> eliminatedTeams;
    private GameState gameState;
    private int mainYLevel;

    public Game(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        this.teamManager = new TeamManager();
        this.noCoreBlocks = new ArrayList<>();
        this.eliminatedTeams = new ArrayList<>();
        this.selectedKit = new HashMap<>();
        this.teams = null;
        this.killPoints = new HashMap<>();
        this.lastHitPlayer = new HashMap<>();
        this.gameManager = Arun.getGameManager();
        this.gameManager.getGames().add(this);
        this.pickaxeUpgrade = new HashMap<>();
        this.coreBlockUpgrade = new HashMap<>();
        this.mainYLevel = 0;
        this.gameState = GameState.LOBBY;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setUpPlayer(Player player, Team team) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(Chat.color("&7You are on the " + Chat.getColorFromName(team.getTeamName()) + team.getTeamName() + " &7team!"));
        Location coreBlock = Arun.getGameAPI().getCoreBlockLocation(gameInfo.getGameName(), team.getTeamName());
        coreBlock.getBlock().setType(Material.COBBLESTONE);
        Location spawn = Arun.getGameAPI().getSpawnLocation(gameInfo.getGameName(), team.getTeamName());
        player.teleport(spawn);
    }

    public void generateRegions(List<Cuboid2D> regions) {
        for (Cuboid2D region : regions) {
            ParkourGenerator parkourGenerator = new ParkourGenerator(region.getLoc1(), region.getLoc2());
            parkourGenerator.generateRegion(Arun.getRegionAPI().getBlockTypes(gameInfo.getGameName()));
            this.mainYLevel = (int) region.getLoc1().getY();
        }
    }

    public void addSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(Chat.color("&c[Arun] &7You have been put in spectator mode!"));
        player.teleport(gameInfo.getLobbyLocation());
    }

    public void startScoreboardTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : gameInfo.getPlayers()) {
                    new GameScoreboard(Arun.getGameManager().getPlayersGame(p), p);
                }
                if (gameState == GameState.ENDED) cancel();
            }
        }.runTaskTimer(Arun.getPlugin(Arun.class), 20L, 20L);
    }

    public void startGame() {
        teams = teamManager.generateTeams(gameInfo.getPlayers(), gameInfo.getTeamMode(), gameInfo.isTwoPlatforms());
        for (Team team : teams) {
            for (Player p : team.getTeamPlayers()) {
                setUpPlayer(p, team);
                giveItems(p);
            }
        }
        generateRegions(Arun.getRegionAPI().getRegions(gameInfo.getGameName()));
        startScoreboardTask();
    }

    public void giveItems(Player player) {
        Kit kit = new Kit(getSelectedKit().getOrDefault(player, "default"));
        player.getInventory().addItem(ItemBuilder.createItem(Material.WOOD_PICKAXE, "Pickaxe"));
        if (kit.getItems() != null) {
            kit.getItems().forEach(itemStack -> player.getInventory().addItem(itemStack));
        }
    }

    public void endGame(Team winners) {
        this.gameState = GameState.ENDED;
        if (winners == null) {
            System.out.println("[Arun] A game has been cancelled due to an issue! (Most likely a server restart) { Game: " + gameInfo.getGameName() + " }");

            resetGame();
            return;
        }
        for (Player p : getGameInfo().getPlayers()) {
            int coinAdd = (Integer) ConfigUtil.getConfigValue("game-rewards.game-played");
            if (coinAdd != 0) {
                EconomyManager.addMoney(p, coinAdd);
                p.sendMessage(Chat.color("&6+" + coinAdd + " coin" + (coinAdd == 1 ? "" : "s") + " for playing a game!"));
            }
            List<String> gameEndMessage = Arrays.asList(Chat.color("&e&lGAME OVER!"), " ", Chat.color("&7&m----------------------"),
                    Chat.color("&e&lWINNERS: " + Chat.getColorFromName(winners.getTeamName()) + winners.getTeamName().toUpperCase()),
                    Chat.color("&e&lGAME: &r&7" + gameInfo.getGameName()), Chat.color("&7&m----------------------"));
            gameEndMessage.forEach(p::sendMessage);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Arun.class), () -> p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()), 20L);
        }
        for (Player p : winners.getTeamPlayers()) {
            List<ItemStack> items = Arrays.asList(p.getInventory().getContents());
            List<ItemStack> armor = Arrays.asList(p.getInventory().getArmorContents());
            List<ItemStack> all = new ArrayList<>();
            all.addAll(items);
            all.addAll(armor);
            int coinAdd = (Integer) ConfigUtil.getConfigValue("game-rewards.game-win");
            if (coinAdd != 0) {
                EconomyManager.addMoney(p, coinAdd);
                p.sendMessage(Chat.color("&6+" + coinAdd + " coin" + (coinAdd == 1 ? "" : "s") + " for winning a game!"));
            }
        }
        resetGame();
    }

    public void resetGame() {
        getLastHitPlayer().clear();
        getTeams().clear();
        getKillPoints().clear();
        getEliminatedTeams().clear();
        getTeamsWithoutCoreBlocks().clear();
        getPickaxeUpgrade().clear();
        getCoreBlockUpgrade().clear();
        getGameInfo().getPlayers().clear();
        getLastHitPlayer().clear();
        Arun.getGameManager().getGames().remove(this);
    }

    public void startTimer() {
        new BukkitRunnable() {
            int seconds = 10;

            @Override
            public void run() {
                if (gameInfo.getPlayers().size() != gameInfo.getTeamMode().getTotalLobbySize()) {
                    gameInfo.getPlayers().forEach(player -> player.sendMessage(Chat.color("&cNot enough players to start! Cancelled!")));
                    cancel();
                }
                if (seconds == 3 || seconds == 2 || seconds == 1 || seconds == 5 || seconds == 10) {
                    for (Player p : gameInfo.getPlayers()) {
                        p.sendMessage(Chat.color("&eThe game will begin in &e&l" + seconds + " &r&esecond" + (seconds == 1 ? "" : "s")));
                    }
                }
                if (seconds == 0) {
                    gameState = GameState.STARTED;
                    startGame();
                    cancel();
                }
                seconds--;
            }
        }.runTaskTimer(Arun.getPlugin(Arun.class), 20L, 20L);
    }

    public void addPlayer(Player player) {
        if (gameInfo.getPlayers().size() == gameInfo.getMaxPlayers()) {
            player.sendMessage(Chat.color("&c[Arun] &7That game is already full!"));
            return;
        }
        gameManager.getLastLocation().put(player, player.getLocation());
        gameInfo.getPlayers().add(player);
        player.teleport(gameInfo.getLobbyLocation());
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().setItem(0, ItemBuilder.createItem(Material.BOW, "&cKit Selector"));
        player.getInventory().setItem(8, ItemBuilder.createItem(Material.BED, "&cLeave Game"));
        for (Player p : gameInfo.getPlayers()) {
            p.sendMessage(Chat.color("&e" + player.getName() + " &7joined the game! &8[&e" + gameInfo.getPlayers().size() + "&8/&e" + gameInfo.getMaxPlayers() + "&8]"));
        }
        if (gameInfo.getPlayers().size() == gameInfo.getMaxPlayers()) {
            startTimer();
        }
    }

    public Team getPlayersTeam(Player player) {
        AtomicReference<Team> result = new AtomicReference<>();
        getTeams().forEach(team -> {
            if (team.getTeamPlayers().contains(player)) {
                result.set(team);
            }
        });
        return result.get();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void removePlayer(Player player) {
        if (gameInfo.getPlayers().contains(player)) {
            gameInfo.getPlayers().remove(player);
            player.sendMessage(Chat.color("&eYou left the game!"));
            player.teleport(gameManager.getLastLocation().get(player));
            for (Player p : gameInfo.getPlayers()) {
                p.sendMessage(Chat.color("&e" + player.getName() + " &7left the game! &8[&e" + gameInfo.getPlayers().size() + "&8/&e" + gameInfo.getMaxPlayers() + "&8]"));
            }
        } else {
            Bukkit.getLogger().severe("Couldn't find player while trying to remove user from game!");
        }
    }

    public HashMap<Player, String> getSelectedKit() {
        return selectedKit;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public HashMap<Player, Player> getLastHitPlayer() {
        return lastHitPlayer;
    }

    public List<Team> getTeamsWithoutCoreBlocks() {
        return noCoreBlocks;
    }

    public List<Team> getEliminatedTeams() {
        return eliminatedTeams;
    }

    public HashMap<Player, Integer> getKillPoints() {
        return killPoints;
    }

    public HashMap<Player, GuiShop.PickaxeUpgrade> getPickaxeUpgrade() {
        return pickaxeUpgrade;
    }

    public HashMap<String, GuiShop.CoreBlockUpgrade> getCoreBlockUpgrade() {
        return coreBlockUpgrade;
    }

    public int getMainYLevel() {
        return mainYLevel;
    }

}
