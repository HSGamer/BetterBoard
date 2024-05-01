package me.hsgamer.betterboard;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.permission.PermissionComponent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions extends PermissionComponent {
    public static final Permission RELOAD = new Permission("betterboard.reload", PermissionDefault.OP);
    public static final Permission TOGGLE = new Permission("betterboard.toggle", PermissionDefault.OP);

    public Permissions(BasePlugin plugin) {
        super(plugin);
    }
}
