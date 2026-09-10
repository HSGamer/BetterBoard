package me.hsgamer.betterboard.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.board.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBoardManager implements Loadable, PostEnable {
    private final Map<UUID, Board> boardMap = new ConcurrentHashMap<>();
    private final BetterBoard instance;

    public PlayerBoardManager(BetterBoard instance) {
        this.instance = instance;
    }

    public boolean hasBoard(Player player) {
        return boardMap.containsKey(player.getUniqueId());
    }

    public void addBoard(Player player) {
        Board replacement = new Board(instance, player);
        Optional.ofNullable(boardMap.put(player.getUniqueId(), replacement)).ifPresent(Board::cancel);
    }

    public void removeBoard(Player player) {
        Optional.ofNullable(boardMap.remove(player.getUniqueId())).ifPresent(Board::cancel);
    }

    public void clearAll() {
        boardMap.values().forEach(Board::cancel);
        boardMap.clear();
    }

    @Override
    public void postEnable() {
        Bukkit.getOnlinePlayers().forEach(this::addBoard);
    }

    @Override
    public void disable() {
        clearAll();
    }
}
