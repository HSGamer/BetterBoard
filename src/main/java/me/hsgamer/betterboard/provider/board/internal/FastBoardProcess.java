package me.hsgamer.betterboard.provider.board.internal;

import fr.mrmicky.fastboard.FastBoard;
import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FastBoardProcess implements BoardProcess {
    private final Player player;
    private final FastBoardProvider provider;
    private FastBoard fastBoard;

    public FastBoardProcess(Player player, FastBoardProvider provider) {
        this.player = player;
        this.provider = provider;
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
    public void stop() {
        if (fastBoard != null && !fastBoard.isDeleted()) {
            fastBoard.delete();
            fastBoard = null;
        }
    }

    @Override
    public BoardProvider getProvider() {
        return provider;
    }

    @Override
    public void update() {
        Optional<BoardFrame> optional = provider.fetch(player);
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