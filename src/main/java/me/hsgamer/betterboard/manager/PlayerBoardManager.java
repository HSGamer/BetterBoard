package me.hsgamer.betterboard.manager;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.board.Board;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBoardManager {
    private final Map<UUID, Board> boardMap = new ConcurrentHashMap<>();
    private final BetterBoard instance;

    public PlayerBoardManager(BetterBoard instance) {
        this.instance = instance;
    }

    public void addBoard(Player player) {
        boardMap.put(player.getUniqueId(), new Board(instance, player));
    }

    public void removeBoard(Player player) {
        Optional.ofNullable(boardMap.remove(player.getUniqueId())).ifPresent(Board::cancel);
    }

    public void clearAll() {
        boardMap.values().forEach(Board::cancel);
        boardMap.clear();
    }
}
