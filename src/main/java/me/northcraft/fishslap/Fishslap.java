package me.northcraft.fishslap;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fishslap extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.gameManager = new GameManager(this);
        getServer().getPluginManager().registerEvents(new FishListener(this, gameManager), this);

        if (getCommand("fishslap") != null) {
            getCommand("fishslap").setExecutor(new FishslapCommand(this));
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void saveLoc(String path, Location loc) {
        getConfig().set(path + ".world", loc.getWorld().getName());
        getConfig().set(path + ".x", loc.getX());
        getConfig().set(path + ".y", loc.getY());
        getConfig().set(path + ".z", loc.getZ());
        getConfig().set(path + ".yaw", loc.getYaw());
        getConfig().set(path + ".pitch", loc.getPitch());
        saveConfig();
    }

    public Location getLoc(String path) {
        if (!getConfig().contains(path + ".world")) return null;
        return new Location(
                getServer().getWorld(getConfig().getString(path + ".world")),
                getConfig().getDouble(path + ".x"),
                getConfig().getDouble(path + ".y"),
                getConfig().getDouble(path + ".z"),
                (float) getConfig().getDouble(path + ".yaw"),
                (float) getConfig().getDouble(path + ".pitch")
        );
    }
}