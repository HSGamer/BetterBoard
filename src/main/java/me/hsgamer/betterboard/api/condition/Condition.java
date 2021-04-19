package me.hsgamer.betterboard.api.condition;

import org.bukkit.entity.Player;

public interface Condition {
    default void clear() {
        // EMPTY
    }

    boolean check(Player player);
}
