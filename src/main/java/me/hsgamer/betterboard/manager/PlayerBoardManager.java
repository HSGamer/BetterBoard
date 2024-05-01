package me.hsgamer.betterboard.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.board.Board;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerBoardManager implements Loadable {
    private final Map<UUID, Board> boardMap = Collections.synchronizedMap(new HashMap<>());
    private final BetterBoard instance;

    public PlayerBoardManager(BetterBoard instance) {
        this.instance = instance;
    }

    public boolean hasBoard(Player player) {
        return boardMap.containsKey(player.getUniqueId());
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

    @Override
    public void disable() {
        clearAll();
    }
}
