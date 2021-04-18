package me.hsgamer.betterboard.api.provider;

import org.bukkit.entity.Player;

import java.util.List;

public interface BoardProvider {
    default boolean canFetch(Player player) {
        return true;
    }

    String fetchTitle(Player player);

    List<String> fetchLines(Player player);
}
