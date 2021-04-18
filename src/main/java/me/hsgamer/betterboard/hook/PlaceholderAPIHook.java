package me.hsgamer.betterboard.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHook {
    private static Plugin placeholderAPI;

    private PlaceholderAPIHook() {
        // EMPTY
    }

    public static boolean setupPlugin() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return false;
        }

        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        return placeholderAPI != null;
    }

    public static String setPlaceholders(String message, OfflinePlayer executor) {
        if (placeholderAPI == null) {
            return message;
        }
        return PlaceholderAPI.setPlaceholders(executor, message);
    }
}
