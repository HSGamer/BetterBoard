package me.hsgamer.betterboard;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {
    public static final Permission RELOAD = new Permission("betterboard.reload", PermissionDefault.OP);
    public static final Permission TOGGLE = new Permission("betterboard.toggle", PermissionDefault.OP);

    private Permissions() {
        // EMPTY
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(RELOAD);
        Bukkit.getPluginManager().addPermission(TOGGLE);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(RELOAD);
        Bukkit.getPluginManager().removePermission(TOGGLE);
    }
}
