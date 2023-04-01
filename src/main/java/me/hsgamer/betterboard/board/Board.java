package me.hsgamer.betterboard.board;

import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.config.MainConfig;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Board implements Runnable {
    private final Player player;
    private final BetterBoard instance;
    private final AtomicReference<BoardProcess> currentProcess = new AtomicReference<>();
    private final Task task;

    public Board(BetterBoard instance, Player player) {
        this.instance = instance;
        this.player = player;

        long update = MainConfig.UPDATE_TICKS.getValue();
        boolean async = MainConfig.UPDATE_ASYNC.getValue();
        update = Math.max(update, 0);
        task = Scheduler.CURRENT.runTaskTimer(instance, this, update, update, async);
    }

    public void cancel() {
        task.cancel();
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
