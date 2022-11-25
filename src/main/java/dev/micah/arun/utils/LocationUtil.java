package dev.micah.arun.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static String asString(Location location) {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return worldName + ";" + x + ";" + y + ";" + z;
    }

    public static String asDisplayString(Location location) {
        if (location == null) return "";
        return "X: " + (int) location.getX() + ", Y: " + (int) location.getY() + ", Z: " + (int) location.getZ();
    }

    public static Location fromString(String string) {
        if (string == null) return null;
        String[] values = string.split(";");
        return new Location(Bukkit.getWorld(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
    }

}
