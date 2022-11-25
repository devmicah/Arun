package dev.micah.arun.addons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * Arun Addon API
 * <p>
 * Use this API to listen and communicate with the arun plugin.
 * You can create custom messages, scoreboards, events, and a
 * lot more by simply understanding the events and methods.
 * Support and wiki coming soon for developers.
 */

public class ArunAddon {

    /**
     * Arun main class instance
     **/
    private static final Plugin ARUN = Bukkit.getPluginManager().getPlugin("Arun");

    /**
     * Addon instance for future uses after enable
     **/
    private static Addon ADDON = null;

    /**
     * Loads an addon into the Arun injector
     *
     * @param addon The addon you are loading
     */
    public void loadAddon(Addon addon) {
        initFiles();
        File dir = new File(ARUN.getDataFolder() + "/addons");
        File file = getPossibleJar(addon, dir);
        if (file == null) return;
        System.out.println("1");
        if (getFileExtension(file).equals("jar")) {
            System.out.println("2");
            /** Deprecated because spigot does not like it when you register plugins this way but we are fine **/
            JavaPluginLoader loader = new JavaPluginLoader(Bukkit.getServer());
            try {
                System.out.println("3");
                Plugin theAddon = loader.loadPlugin(file);
                loader.enablePlugin(theAddon);
                ADDON = addon;
            } catch (InvalidPluginException x) {
                x.printStackTrace();
            }
        }
        addon.bootup(); /** Bootup last just in case it calls instances of Arun **/
    }

    public static void loadAllAddons() {
        initFiles();
        System.out.println("1");
        File dir = new File(ARUN.getDataFolder() + "/addons");
        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
            if (getFileExtension(file).equals("jar")) {
                JavaPluginLoader loader = new JavaPluginLoader(Bukkit.getServer());
                try {
                    System.out.println("Booted up " + file.getName());
                    Plugin theAddon = loader.loadPlugin(file);
                    loader.enablePlugin(theAddon);
                } catch (InvalidPluginException x) {
                    x.printStackTrace();
                }
            }
        }
    }

    public File getPossibleJar(Addon addon, File dir) {
        if (dir.listFiles() == null) return null;
        for (File file : dir.listFiles()) {
            if (file.getName().contains(addon.getAddonName())) {
                return file;
            }
        }
        return null;
    }

    /**
     * Shuts down an addon when called, requires it to be running though
     */
    public void unloadAddon() {
        if (ADDON == null) return;
        ADDON.shutdown();
        Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(ADDON.getAddonName()));
    }

    /**
     * Simple and common file extension method
     *
     * @param file File which you want to check the extension of
     * @return The extension of the file (if any)
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    /**
     * Loads the directory needed to house all of the addons
     */
    private static void initFiles() {
        File addonDir = new File(ARUN.getDataFolder() + "/addons");
        if (!addonDir.exists()) {
            addonDir.mkdirs();
        }
    }

}
