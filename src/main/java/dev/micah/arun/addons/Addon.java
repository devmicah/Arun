package dev.micah.arun.addons;

public interface Addon {

    void bootup();

    void shutdown();

    String getAddonName();

    double getVersion();

}
