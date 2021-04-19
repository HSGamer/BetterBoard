package me.hsgamer.betterboard.condition;

import me.hsgamer.betterboard.api.condition.ConfigurableCondition;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionCondition implements ConfigurableCondition {
    private final List<String> list = new ArrayList<>();

    @Override
    public boolean check(Player player) {
        return list.stream()
                .map(s -> VariableManager.setVariables(s, player.getUniqueId()))
                .allMatch(s -> hasPermission(player, s));
    }

    private boolean hasPermission(Player player, String permission) {
        if (permission.startsWith("-")) {
            return !player.hasPermission(permission.substring(1).trim());
        } else {
            return player.hasPermission(permission);
        }
    }

    @Override
    public void loadFromObject(Object object) {
        this.list.addAll(CollectionUtils.createStringListFromObject(object, true));
    }
}
