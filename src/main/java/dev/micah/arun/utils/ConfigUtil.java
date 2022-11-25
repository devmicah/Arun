package dev.micah.arun.utils;

import dev.micah.arun.Arun;

public class ConfigUtil {

    public static Object getConfigValue(String path) {
        return Arun.getPlugin(Arun.class).getConfig().get(path);
    }

}
