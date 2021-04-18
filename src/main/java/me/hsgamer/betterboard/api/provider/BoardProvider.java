package me.hsgamer.betterboard.api.provider;

import me.hsgamer.betterboard.api.BoardFrame;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface BoardProvider {
    default boolean canFetch(Player player) {
        return true;
    }

    Optional<BoardFrame> fetch(Player player);
}
