package dev.micah.arun.utils;

import org.bukkit.ChatColor;

public class Chat {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ChatColor getColorFromName(String name) {
        for (ChatColor value : ChatColor.values()) {
            if (value.name().toLowerCase().equals(name.toLowerCase())) {
                return value;
            }
        }
        return null;
    }

}
