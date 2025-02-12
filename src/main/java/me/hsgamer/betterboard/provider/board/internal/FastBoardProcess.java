package me.hsgamer.betterboard.provider.board.internal;

import me.hsgamer.betterboard.api.provider.BoardProcess;
import me.hsgamer.betterboard.api.provider.BoardProvider;
import me.hsgamer.betterboard.provider.board.FastBoardProvider;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public abstract class FastBoardProcess implements BoardProcess {
    protected final FastBoardProvider provider;
    private final Player player;
    private FastBoardOperator operator;

    protected FastBoardProcess(Player player, FastBoardProvider provider) {
        this.player = player;
        this.provider = provider;
    }

    protected abstract FastBoardOperator createOperator(Player player);

    @Override
    public void stop() {
        if (operator != null && !operator.isDeleted()) {
            operator.delete();
            operator = null;
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
                if (operator == null || operator.isDeleted()) {
                    operator = createOperator(player);
                }
                operator.updateTitle(frame.getTitle());
                operator.updateLines(frame.getLines());
            } else if (operator != null) {
                if (!operator.isDeleted()) {
                    operator.delete();
                }
                operator = null;
            }
        } catch (RuntimeException ignored) {
            // IGNORED
        }
    }

    public interface FastBoardOperator {
        void updateTitle(String title);

        void updateLines(List<String> lines);

        boolean isDeleted();

        void delete();
    }
}