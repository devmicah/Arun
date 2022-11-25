package dev.micah.arun.kits;

import dev.micah.arun.io.YamlFile;

public class KitManager {

    private YamlFile yamlFile;

    public KitManager() {
        this.yamlFile = new YamlFile("kits");
    }

    public YamlFile getYamlFile() {
        return yamlFile;
    }

}
