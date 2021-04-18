package me.hsgamer.betterboard.board;

import fr.mrmicky.fastboard.FastBoard;
import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class Board extends BukkitRunnable {
    private final Player player;
    private final BetterBoard instance;
    private FastBoard fastBoard;

    public Board(BetterBoard instance, Player player) {
        this.instance = instance;
        this.player = player;

        int update = MainConfig.UPDATE_TICKS.getValue();
        boolean async = MainConfig.UPDATE_ASYNC.getValue();
        update = Math.max(update, 0);
        if (async) {
            runTaskTimerAsynchronously(instance, update, update);
        } else {
            runTaskTimer(instance, update, update);
        }
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        if (fastBoard != null && !fastBoard.isDeleted()) {
            fastBoard.delete();
        }
    }

    @Override
    public void run() {
        Optional<BoardFrame> optional = instance.getBoardProviderManager().getProvider(player).flatMap(boardProvider -> boardProvider.fetch(player));
        if (optional.isPresent()) {
            BoardFrame frame = optional.get();
            if (fastBoard == null || fastBoard.isDeleted()) {
                fastBoard = new FastBoard(player);
            }
            fastBoard.updateTitle(frame.getTitle());
            fastBoard.updateLines(frame.getLines());
        } else if (fastBoard != null && !fastBoard.isDeleted()) {
            fastBoard.delete();
            fastBoard = null;
        }
    }
}
