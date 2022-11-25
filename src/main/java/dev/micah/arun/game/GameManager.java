package dev.micah.arun.game;

import dev.micah.arun.Arun;
import dev.micah.arun.utils.AttachedBoolean;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameManager {

    private List<Game> games;
    private HashMap<Player, Location> lastLocation;

    public GameManager() {
        this.games = new ArrayList<>();
        this.lastLocation = new HashMap<>();
    }

    public List<Game> getGames() {
        return games;
    }

    public Game getPlayersGame(Player player) {
        for (Game game : games) {
            if (game.getGameInfo().getPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }

    public boolean isGameReady(String gameName) {
        AttachedBoolean coreBlocks = new AttachedBoolean(Arun.getGameAPI().getTeamsWithCoreBlocks(gameName).size() ==
                (Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4), "&fSet Core Block Locations");
        AttachedBoolean region = new AttachedBoolean(Arun.getRegionAPI().getRegions(gameName).size() > 0, "&fSet at least 1 Region");
        AttachedBoolean regionBlocks = new AttachedBoolean(Arun.getRegionAPI().getBlockTypes(gameName).size() > 0, "&fSet at least 1 Region Block");
        AttachedBoolean spawnLocations = new AttachedBoolean(Arun.getGameAPI().getSpawnLocations(gameName).size() ==
                (Arun.getGameAPI().isTwoPlatforms(gameName) ? 2 : 4), "&fSet the needed amount of spawns");
        AttachedBoolean lobbyLocation = new AttachedBoolean(Arun.getGameAPI().getLobbySpawn(gameName) != null, "&fSet a Lobby Location");

        List<AttachedBoolean> all = Arrays.asList(coreBlocks, region, regionBlocks, spawnLocations, lobbyLocation);
        List<AttachedBoolean> need = new ArrayList<>();

        all.forEach(attachedBoolean -> {
            if (!attachedBoolean.getBoolean()) {
                need.add(attachedBoolean);
            }
        });
        return need.size() == 0;
    }

    public Game getGameByName(String name) {
        for (Game game : games) {
            if (game.getGameInfo().getGameName().equals(name)) {
                return game;
            }
        }
        Game newGame = new Game(getGameInfo(name));
        games.add(newGame);
        return newGame;
    }

    public HashMap<Player, Location> getLastLocation() {
        return lastLocation;
    }

    public GameInfo getGameInfo(String gameName) {
        return new GameInfo(gameName,
                new ArrayList<>(),
                Arun.getGameAPI().getTeamMode(gameName),
                Arun.getGameAPI().isTwoPlatforms(gameName),
                Arun.getGameAPI().getCoreBlockLocations(gameName),
                Arun.getGameAPI().getLobbySpawn(gameName),
                Arun.getGameAPI().getSpawnLocations(gameName));
    }

}
