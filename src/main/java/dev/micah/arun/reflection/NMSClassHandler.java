package dev.micah.arun.reflection;

import org.bukkit.Bukkit;

public class NMSClassHandler {

    private final static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static String getVersion() {
        return version;
    }

    public static Class<?> getClass(String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean containsVersion(String... versions) {
        for (String s : versions) {
            if (version.toLowerCase().contains(s)) {
                return true;
            }
        }
        return false;
    }

}
