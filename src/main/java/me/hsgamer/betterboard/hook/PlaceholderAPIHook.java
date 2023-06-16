package me.hsgamer.betterboard.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {

    private PlaceholderAPIHook() {
        // EMPTY
    }

    public static boolean setupPlugin() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static String setPlaceholders(String message, OfflinePlayer executor) {
        return PlaceholderAPI.setPlaceholders(executor, message);
    }
}
