package dev.micah.arun.api.game;

import dev.micah.arun.io.YamlFile;
import dev.micah.arun.team.TeamMode;
import dev.micah.arun.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class GameAPI {

    private YamlConfiguration config;
    private YamlFile yamlFile;

    public GameAPI() {
        this.yamlFile = new YamlFile("gamedata");
        this.config = yamlFile.getConfig();
    }

    public boolean doesGameExist(String name) {
        if (config.getConfigurationSection("games") == null) return false;
        Set<String> keys = config.getConfigurationSection("games").getKeys(false);
        if (keys.isEmpty()) return false;
        for (String s : keys) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void createGame(String name) {
        config.set("games." + name, null);
        config.set("games." + name + ".twoPlatforms", true);
        config.set("games." + name + ".mode", TeamMode.SOLO.name().toLowerCase());
        yamlFile.save();
    }

    public boolean setLobbySpawn(String gameName, Location location) {
        if (doesGameExist(gameName)) {
            config.set("games." + gameName + ".lobby", LocationUtil.asString(location));
            yamlFile.save();
            return true;
        }
        return false;
    }

    public boolean addSpawnLocation(String gameName, String team, Location location) {
        if (doesGameExist(gameName)) {
            List<String> locations = config.getStringList("games." + gameName + ".spawns");
            locations.add(team + "%" + LocationUtil.asString(location));
            config.set("games." + gameName + ".spawns", locations);
            yamlFile.save();
        }
        return false;
    }

    public void deleteCoreBlockLocation(String gameName, String team) {
        List<String> coreBlockList = config.getStringList("games." + gameName + ".core-blocks");
        AtomicReference<String> toRemove = new AtomicReference<>();
        coreBlockList.forEach(s -> {
            if (s.split("%")[0].toUpperCase().equals(team.toUpperCase())) {
                toRemove.set(s);
            }
        });
        coreBlockList.remove(toRemove.get());
        config.set("games." + gameName + ".core-blocks", coreBlockList);
        yamlFile.save();
    }

    public boolean addCoreBlockLocation(String gameName, Location location, String team) {
        if (doesGameExist(gameName)) {
            List<String> coreBlocks = config.getStringList("games." + gameName + ".core-blocks");
            coreBlocks.add(team + "%" + LocationUtil.asString(location));
            config.set("games." + gameName + ".core-blocks", coreBlocks);
            yamlFile.save();
        }
        return false;
    }

    public boolean setTwoPlatforms(String gameName, boolean twoPlatforms) {
        if (doesGameExist(gameName)) {
            config.set("games." + gameName + ".twoPlatforms", twoPlatforms);
            yamlFile.save();
            return true;
        }
        return false;
    }

    public boolean setTeamMode(String gameName, TeamMode teamMode) {
        if (doesGameExist(gameName)) {
            config.set("games." + gameName + ".mode", teamMode.name().toLowerCase());
            yamlFile.save();
            return true;
        }
        return false;
    }

    public TeamMode getTeamMode(String gameName) {
        if (doesGameExist(gameName)) {
            for (TeamMode mode : TeamMode.values()) {
                if (mode.name().toLowerCase().contains(config.getString("games." + gameName + ".mode"))) {
                    return mode;
                }
            }
        }
        return null;
    }

    public void deleteSpawn(String gameName, String team) {
        List<String> spawnList = config.getStringList("games." + gameName + ".spawns");
        AtomicReference<String> toRemove = new AtomicReference<>();
        spawnList.forEach(s -> {
            if (s.split("%")[0].toUpperCase().equals(team.toUpperCase())) {
                toRemove.set(s);
            }
        });
        spawnList.remove(toRemove.get());
        config.set("games." + gameName + ".spawns", spawnList);
        yamlFile.save();
    }

    public List<Location> getSpawnLocations(String gameName) {
        if (doesGameExist(gameName)) {
            List<Location> locations = new ArrayList<>();
            for (String locationString : config.getStringList("games." + gameName + ".spawns")) {
                locations.add(LocationUtil.fromString(locationString.split("%")[1]));
            }
            return locations;
        }
        return null;
    }

    public Location getSpawnLocation(String gameName, String team) {
        for (String locationString : config.getStringList("games." + gameName + ".spawns")) {
            if (locationString.split("%")[0].toUpperCase().equals(team.toUpperCase())) {
                return LocationUtil.fromString(locationString.split("%")[1]);
            }
        }
        return null;
    }

    public Location getLobbySpawn(String gameName) {
        if (doesGameExist(gameName)) {
            return LocationUtil.fromString(config.getString("games." + gameName + ".lobby"));
        }
        return null;
    }

    public List<String> getTeamsWithCoreBlocks(String gameName) {
        String[] teams = new String[] {"RED", "BLUE", "GREEN", "YELLOW"};
        List<String> result = new ArrayList<>();
        for (String s : teams) {
            if (getCoreBlockLocation(gameName, s) != null) {
                result.add(s);
            }
        }
        return result;
    }

    public Location getCoreBlockLocation(String gameName, String team) {
        if (doesGameExist(gameName)) {
            for (String locationString : config.getStringList("games." + gameName + ".core-blocks")) {
                if (locationString.split("%")[0].toLowerCase().contains(team.toLowerCase())) {
                    return LocationUtil.fromString(locationString.split("%")[1]);
                }
            }
        }
        return null;
    }

    public List<Location> getCoreBlockLocations(String gameName) {
        if (doesGameExist(gameName)) {
            List<Location> locs = new ArrayList<>();
            for (String locationString : config.getStringList("games." + gameName + ".core-blocks")) {
                locs.add(LocationUtil.fromString(locationString.split("%")[1]));
            }
            return locs;
        }
        return null;
    }

    public boolean isTwoPlatforms(String gameName) {
        if (doesGameExist(gameName)) {
            return config.getBoolean("games." + gameName + ".twoPlatforms");
        }
        return false;
    }

    public void publish(String gameName) {
        List<String> publishedGames = config.getStringList("published-games");
        publishedGames.add(gameName);
        config.set("published-games", publishedGames);
        yamlFile.save();
    }

    public List<String> getPublishedGames() {
        return config.getStringList("published-games");
    }

}
