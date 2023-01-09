package me.hsgamer.betterboard.board;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.config.MainConfig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Board extends BukkitRunnable {
    private final Player player;
    private final BetterBoard instance;
    private final AtomicReference<BoardProcess> currentProcess = new AtomicReference<>();

    public Board(BetterBoard instance, Player player) {
        this.instance = instance;
        this.player = player;

        long update = MainConfig.UPDATE_TICKS.getValue();
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
        Optional.ofNullable(currentProcess.getAndSet(null)).ifPresent(BoardProcess::stop);
    }

    @Override
    public void run() {
        Optional<BoardProvider> optional = instance.getBoardProviderManager().getProvider(player);
        try {
            BoardProcess process = currentProcess.get();
            if (optional.isPresent()) {
                BoardProvider provider = optional.get();
                if (process != null) {
                    if (process.getProvider() == provider) {
                        process.update();
                        return;
                    } else {
                        process.stop();
                    }
                }
                process = provider.createProcess(player);
                process.init();
                currentProcess.set(process);
            } else {
                if (process == null) return;
                process.stop();
                currentProcess.set(null);
            }
        } catch (RuntimeException ignored) {
            // IGNORED
        }
    }
}
