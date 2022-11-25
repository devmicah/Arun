package dev.micah.arun;

import dev.micah.arun.api.game.GameAPI;
import dev.micah.arun.api.region.RegionAPI;
import dev.micah.arun.bstats.Metrics;
import dev.micah.arun.economy.EconomyManager;
import dev.micah.arun.game.GameListener;
import dev.micah.arun.game.GameManager;
import dev.micah.arun.game.setup.GameCommands;
import dev.micah.arun.game.setup.GameTabCompleter;
import dev.micah.arun.game.setup.items.ItemCoreBlockWand;
import dev.micah.arun.game.setup.items.ItemRegionWand;
import dev.micah.arun.game.setup.items.ItemShopEntityWand;
import dev.micah.arun.game.setup.items.ItemSpawnLocationButton;
import dev.micah.arun.generator.GeneratorIO;
import dev.micah.arun.gui.game.GuiCoreBlockManager;
import dev.micah.arun.gui.game.GuiGameManager;
import dev.micah.arun.gui.join.GuiGameJoin;
import dev.micah.arun.gui.publish.GuiPublishManager;
import dev.micah.arun.gui.region.GuiBlockManager;
import dev.micah.arun.gui.region.GuiRegionEditor;
import dev.micah.arun.gui.region.GuiRegionManager;
import dev.micah.arun.io.YamlFile;
import dev.micah.arun.kits.KitManager;
import dev.micah.arun.kits.gui.GuiKitSelector;
import dev.micah.arun.kits.gui.GuiKitShop;
import dev.micah.arun.kits.gui.GuiViewKit;
import dev.micah.arun.shop.GuiShop;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public final class Arun extends JavaPlugin {

    private GeneratorIO generatorIO;

    private static GameAPI gameAPI;
    private static RegionAPI regionAPI;
    private static GameManager gameManager;
    private static KitManager kitManager;

    private YamlFile scoreboardFile;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        Metrics metrics = new Metrics(this, 11179);

        scoreboardFile = new YamlFile("scoreboard");
        if (scoreboardFile.getConfig().getStringList("scoreboard").size() == 0) {
            scoreboardFile.getConfig().set("scoreboard", Arrays.asList(" ",
                    "&c&lGame: &r&7%game_name%",
                    "  ",
                    "&c&lKill Points: &r&7%kill_points%",
                    "&c&lCore Block: &r%core_block%",
                    "   ",
                    "https://yourserver.com/"));
            scoreboardFile.save();
        }

        generatorIO = new GeneratorIO(this);
        generatorIO.initFiles();

        gameAPI = new GameAPI();
        regionAPI = new RegionAPI(generatorIO);
        kitManager = new KitManager();

        Bukkit.getPluginManager().registerEvents(new ItemRegionWand(), this);
        Bukkit.getPluginManager().registerEvents(new ItemCoreBlockWand(), this);
        Bukkit.getPluginManager().registerEvents(new ItemShopEntityWand(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSpawnLocationButton(), this);

        Bukkit.getPluginManager().registerEvents(new GuiRegionManager(), this);
        Bukkit.getPluginManager().registerEvents(new GuiBlockManager(), this);
        Bukkit.getPluginManager().registerEvents(new GuiRegionEditor(), this);
        Bukkit.getPluginManager().registerEvents(new GuiGameManager(), this);
        Bukkit.getPluginManager().registerEvents(new GuiCoreBlockManager(), this);
        Bukkit.getPluginManager().registerEvents(new GuiPublishManager(), this);
        Bukkit.getPluginManager().registerEvents(new GuiGameJoin(), this);
        Bukkit.getPluginManager().registerEvents(new GuiShop(), this);
        Bukkit.getPluginManager().registerEvents(new GuiKitSelector(), this);
        Bukkit.getPluginManager().registerEvents(new GuiViewKit(), this);
        Bukkit.getPluginManager().registerEvents(new GuiKitShop(), this);

        Bukkit.getPluginManager().registerEvents(new GameListener(), this);

        getCommand("arun").setExecutor(new GameCommands());
        getCommand("arun").setTabCompleter(new GameTabCompleter());

        File addonDir = new File(getDataFolder() + "/addons");
        if (!addonDir.exists()) addonDir.mkdirs();

        new EconomyManager();

        gameManager = new GameManager();
        //ArunAddon.loadAllAddons();

        Bukkit.getLogger().info("+--------------------+");
        Bukkit.getLogger().info("Arun has been loaded!");
        Bukkit.getLogger().info("Version: 1.4");
        Bukkit.getLogger().info("+--------------------+");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static GameAPI getGameAPI() {
        return gameAPI;
    }

    public static RegionAPI getRegionAPI() {
        return regionAPI;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static KitManager getKitManager() {
        return kitManager;
    }

    public YamlFile getScoreboardFile() {
        return scoreboardFile;
    }

}
