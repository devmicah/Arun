package dev.micah.arun.game;

import dev.micah.arun.team.TeamMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class GameInfo {

    private List<Player> players;
    private TeamMode teamMode;
    private boolean twoPlatforms;
    private List<Location> obsidianLocations;
    private Location lobbyLocation;
    private String gameName;
    private List<Location> spawnLocations;

    public GameInfo(String gameName, List<Player> players, TeamMode teamMode, boolean twoPlatforms, List<Location> obsidianLocations, Location lobbyLocation, List<Location> spawnLocations) {
        this.gameName = gameName;
        this.players = players;
        this.teamMode = teamMode;
        this.twoPlatforms = twoPlatforms;
        this.obsidianLocations = obsidianLocations;
        this.lobbyLocation = lobbyLocation;
        this.spawnLocations = spawnLocations;
    }

    public String getGameName() {
        return gameName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TeamMode getTeamMode() {
        return teamMode;
    }

    public boolean isTwoPlatforms() {
        return twoPlatforms;
    }

    public List<Location> getCoreBlockLocations() {
        return obsidianLocations;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public int getMaxPlayers() {
        return twoPlatforms ? teamMode.getTotalLobbySize() : teamMode.getTotalLobbySize() * 2;
    }

}
