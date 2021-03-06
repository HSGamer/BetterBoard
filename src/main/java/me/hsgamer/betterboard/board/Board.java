package me.hsgamer.betterboard.board;

import fr.mrmicky.fastboard.FastBoard;
import me.hsgamer.betterboard.BetterBoard;
import me.hsgamer.betterboard.api.BoardFrame;
import me.hsgamer.betterboard.config.MainConfig;
import org.bukkit.Bukkit;
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

        long update = MainConfig.UPDATE_TICKS.getValue();
        boolean async = MainConfig.UPDATE_ASYNC.getValue();
        update = Math.max(update, 0);
        if (async) {
            runTaskTimerAsynchronously(instance, update, update);
        } else {
            runTaskTimer(instance, update, update);
        }
    }

    private static FastBoard createBoard(Player player) {
        return new FastBoard(player) {
            @Override
            protected boolean hasLinesMaxLength() {
                if (super.hasLinesMaxLength()) {
                    return true;
                } else if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
                    // noinspection unchecked
                    return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(getPlayer()) < com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13.getVersion();
                } else if (Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
                    return protocolsupport.api.ProtocolSupportAPI.getProtocolVersion(getPlayer()).isBefore(protocolsupport.api.ProtocolVersion.MINECRAFT_1_13);
                }
                return false;
            }
        };
    }

    @Override
    public synchronized void cancel() {
        super.cancel();
        if (fastBoard != null && !fastBoard.isDeleted()) {
            fastBoard.delete();
            fastBoard = null;
        }
    }

    @Override
    public void run() {
        Optional<BoardFrame> optional = instance.getBoardProviderManager().getProvider(player).flatMap(boardProvider -> boardProvider.fetch(player));
        try {
            if (optional.isPresent()) {
                BoardFrame frame = optional.get();
                if (fastBoard == null || fastBoard.isDeleted()) {
                    fastBoard = createBoard(player);
                }
                fastBoard.updateTitle(frame.getTitle());
                fastBoard.updateLines(frame.getLines());
            } else if (fastBoard != null) {
                if (!fastBoard.isDeleted()) {
                    fastBoard.delete();
                }
                fastBoard = null;
            }
        } catch (RuntimeException ignored) {
            // IGNORED
        }
    }
}
