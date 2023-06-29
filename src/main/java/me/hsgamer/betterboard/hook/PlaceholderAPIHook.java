package me.hsgamer.betterboard.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {
    private static Boolean available = null;

    private PlaceholderAPIHook() {
        // EMPTY
    }

    public static boolean isAvailable() {
        if (available == null) {
            available = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        }
        return available;
    }

    public static String setPlaceholders(String message, OfflinePlayer executor) {
        return PlaceholderAPI.setPlaceholders(executor, message);
    }
}
