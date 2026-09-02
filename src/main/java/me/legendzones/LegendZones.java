package me.legendzones;

import org.bukkit.plugin.java.JavaPlugin;

public class LegendZones extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("LegendZones включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LegendZones выключен!");
    }
}
