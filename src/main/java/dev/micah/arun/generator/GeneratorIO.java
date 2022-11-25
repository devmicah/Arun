package dev.micah.arun.generator;

import dev.micah.arun.Arun;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GeneratorIO {

    private Arun plugin;

    private File file;
    private YamlConfiguration yamlConfiguration;

    public GeneratorIO(Arun plugin) {
        this.plugin = plugin;
    }

    public void initFiles() {
        file = new File(plugin.getDataFolder(), "locationdata.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfig() {
        return yamlConfiguration;
    }

    public void save() {
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
