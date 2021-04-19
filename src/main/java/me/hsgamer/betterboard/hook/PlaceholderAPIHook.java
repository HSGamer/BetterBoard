package me.hsgamer.betterboard.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {

    private PlaceholderAPIHook() {
        // EMPTY
    }

    public static boolean setupPlugin() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return false;
        }
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static boolean hasPlaceholders(String message) {
        return PlaceholderAPI.containsPlaceholders(message);
    }

    public static String setPlaceholders(String message, OfflinePlayer executor) {
        return PlaceholderAPI.setPlaceholders(executor, message);
    }
}
