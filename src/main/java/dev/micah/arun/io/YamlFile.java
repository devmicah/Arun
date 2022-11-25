package dev.micah.arun.io;

import dev.micah.arun.Arun;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlFile {

    private File file;
    private YamlConfiguration yamlConfiguration;

    public YamlFile(String fileName) {
        this.file = new File(Arun.getPlugin(Arun.class).getDataFolder(), fileName + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return yamlConfiguration;
    }

    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
