package me.hsgamer.betterboard.api.provider;

import org.bukkit.entity.Player;

public interface BoardProvider {
    default boolean canFetch(Player player) {
        return true;
    }

    default void clear() {
        // EMPTY
    }

    BoardProcess createProcess(Player player);
}
