package me.hsgamer.betterboard;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {
    public static final Permission RELOAD = new Permission("betterboard.reload", PermissionDefault.OP);

    private Permissions() {
        // EMPTY
    }

    public static void register() {
        Bukkit.getPluginManager().addPermission(RELOAD);
    }

    public static void unregister() {
        Bukkit.getPluginManager().removePermission(RELOAD);
    }
}
